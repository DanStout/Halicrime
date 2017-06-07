package ca.danielstout.halicrime

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import org.sql2o.Sql2o
import java.io.File
import java.time.LocalDate

data class ApiCrimeProperties(@JsonProperty("EVT_DATE") val date: String, @JsonProperty("LOCATION") val location: String, @JsonProperty("RUCR_EXT_D") val type: String)
data class ApiCrimeGeometry(@JsonProperty("coordinates") val coords: Array<Double>)
data class ApiCrime(@JsonProperty("properties") val properties: ApiCrimeProperties, @JsonProperty("geometry") val geometry: ApiCrimeGeometry)
data class ApiResponse(@JsonProperty("features") val crimes: Array<ApiCrime>)

data class Crime(val date: LocalDate, val latitude: Double, val longitude: Double, val location: String, val type: String)

val logger = LoggerFactory.getLogger("ca.danielstout.main");

class Config(val dbUser: String, val dbPass: String, val dbPort: Int, val dbHost: String, val dbName: String)
{
    companion object
    {
        fun load(mapper: ObjectMapper): Config
        {
            return mapper.readValue(File("config.json"))
        }
    }
}

fun main(args: Array<String>)
{
    val mapper = ObjectMapper().registerModule(KotlinModule());
    val conf = Config.load(mapper);

    val data = PGSimpleDataSource()
    data.user = conf.dbUser;
    data.password = conf.dbPass;
    data.portNumber = conf.dbPort;
    data.serverName = conf.dbHost;
    data.databaseName = conf.dbName;
    val hsrc = HikariDataSource();
    hsrc.dataSource = data;

    val fly = Flyway();
    fly.dataSource = hsrc;
    val applied = fly.migrate();
    logger.info("Applied $applied migrations");

    val crime = Crime(LocalDate.now(), 44.6488, 63.5752, "123 Fake St.", "Robbery")

    val sql2o = Sql2o(hsrc);
    sql2o.open().use {
        val sql = """
INSERT INTO crimes (committed_at, coordinates, location, type)
VALUES (:date, POINT(:latitude, :longitude), :location, :type);
"""
        it.createQuery(sql).bind(crime).executeUpdate();
    }


    //    logger.debug("Hello!");
    //    embeddedServer(Netty, 8080) {
    //        routing {
    //            get("/") {
    //                call.respondText("Hello, world!", ContentType.Text.Html);
    //            }
    //        }
    //    }.start(wait = true)
    //    "http://catalogue-hrm.opendata.arcgis.com/datasets/f6921c5b12e64d17b5cd173cafb23677_0.geojson".httpGet().responseString { req, resp, result ->
    //        when (result)
    //        {
    //            is Result.Success ->
    //            {
    //                val mapper = ObjectMapper().registerModule(KotlinModule());
    //                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    //                val ret = mapper.readValue(result.value, ApiResponse::class.java)
    //                val data = ret.crimes.map {
    //                    val props = it.properties;
    //                    val coords = it.geometry.coords;
    //                    if (coords.size != 2) null
    //                    else Crime(date = ZonedDateTime.parse(props.date).toLocalDate(), latitude = coords[1], longitude = coords[0], location = props.location, type = props.type)
    //                }.filterNotNull();
    //                println(data);
    //            }
    //            is Result.Failure ->
    //            {
    //                println("Failed to retrieve data: %s".format(result.error))
    //            }
    //        }
    //    }
}
