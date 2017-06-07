package ca.danielstout.halicrime

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.slf4j.LoggerFactory
import java.time.LocalDate

data class ApiCrimeProperties(@JsonProperty("EVT_DATE") val date: String, @JsonProperty("LOCATION") val location: String, @JsonProperty("RUCR_EXT_D") val type: String)
data class ApiCrimeGeometry(@JsonProperty("coordinates") val coords: Array<Double>)
data class ApiCrime(@JsonProperty("properties") val properties: ApiCrimeProperties, @JsonProperty("geometry") val geometry: ApiCrimeGeometry)
data class ApiResponse(@JsonProperty("features") val crimes: Array<ApiCrime>)

data class Crime(val date: LocalDate, val latitude: Double, val longitude: Double, val location: String, val type: String)


val logger = LoggerFactory.getLogger("ca.danielstout.main");

fun main(args: Array<String>)
{
    logger.debug("Hello!");
    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html);
            }
        }
    }.start(wait = true)
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
