package ca.danielstout.halicrime


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.zaxxer.hikari.HikariDataSource
import io.reactivex.schedulers.Schedulers
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import java.io.File
import java.time.*
import javax.sql.DataSource

class App
{
    val log by logger()

    val mapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    val conf = Config.load(mapper)

    val hsrc = getDataSource()

    private fun getDataSource(): DataSource
    {
        val data = PGSimpleDataSource()
        data.user = conf.dbUser
        data.password = conf.dbPass
        data.portNumber = conf.dbPort
        data.serverName = conf.dbHost
        data.databaseName = conf.dbName
        val hsrc = HikariDataSource()
        hsrc.dataSource = data
        return hsrc;
    }

    val sql2o = Sql2oManager.getSql2o(hsrc)
    val repo = CrimeRepo(sql2o)
    private val sched = Scheduler()

    init
    {
        val fly = Flyway()
        fly.dataSource = hsrc
        val applied = fly.migrate()
        log.info("Applied $applied migrations")

        var doFetch = true;
        val last = repo.getLastCrimeFetch()
        if (last == null)
        {
            log.debug("Fetch has never run! Fetching now");
        }
        else if (!last.success)
        {
            log.debug("Last fetch failed! Fetching now");
        }
        else
        {
            val daysSinceLastFetch = Duration.between(last.time, ZonedDateTime.now()).toDays()
            log.debug("Days since last fetch: ${daysSinceLastFetch}")
            if (daysSinceLastFetch < 8)
            {
                doFetch = false;
            }
        }

        if (doFetch) fetch()

        sched.scheduleTaskRepeatingWeekly({ fetch() }, LocalTime.of(18, 25), DayOfWeek.TUESDAY)

        val vertx = Vertx.vertx()
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        val methods = setOf(HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.OPTIONS,
            HttpMethod.HEAD,
            HttpMethod.DELETE,
            HttpMethod.PUT,
            HttpMethod.PATCH)

        val headers = setOf("origin", "content-type", "accept", "authorization")
        val handler = CorsHandler.create("*")
            .allowedMethods(methods)
            .allowedHeaders(headers)
            .allowCredentials(true)
        router.route().handler(handler)

        router.get("/api/crimes").handler { req ->
            log.debug("In handler")
            vertx.executeBlocking<List<Crime>>({
                val crimes = repo.getCrimes()
                it.complete(crimes)
            }, { res ->
                if (res.succeeded())
                {
                    req.response()
                        .json()
                        .end(mapper.writeValueAsString(res.result()))
                }
                else
                {
                    req.response()
                        .setStatusCode(500)
                        .end()
                }
            })
        }
        router.route().handler(StaticHandler.create().setFilesReadOnly(false))
        val port = conf.serverPort;
        server.requestHandler({ router.accept(it) }).listen(port)
        log.info("Server launched on port $port")
    }

    fun fetch()
    {
        val fetcher = CrimeFetcher(mapper)
        fetcher.fetchCrimes()
            .subscribeOn(Schedulers.io())
            .map({ crimes ->
                val added = repo.storeCrimes(crimes);
                CrimeRepo.CrimeFetchResult(ZonedDateTime.now(), true, added)
            })
            .onErrorReturn({ _ ->
                CrimeRepo.CrimeFetchResult(ZonedDateTime.now(), false, 0)
            })
            .subscribe(
                { x ->
                    repo.updateCrimeHistory(x)
                },
                { e ->
                    log.error("Failed to update crime fetch history", e)
                }
            )
    }

}