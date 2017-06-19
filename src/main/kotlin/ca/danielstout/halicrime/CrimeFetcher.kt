package ca.danielstout.halicrime

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.rx.rx_responseString
import io.reactivex.Single
import java.time.ZonedDateTime

class CrimeFetcher(private val mapper: ObjectMapper)
{
    companion object
    {
        private const val url = "http://opendata.arcgis.com/datasets/f6921c5b12e64d17b5cd173cafb23677_0.geojson"
    }

    private val log by logger();

    private data class ApiCrimeProperties(@JsonProperty("EVT_DATE") val date: String,
        @JsonProperty("LOCATION") val location: String, @JsonProperty("RUCR_EXT_D") val type: String)

    private data class ApiCrimeGeometry(@JsonProperty("coordinates") val coords: Array<Double>)

    private data class ApiCrime(@JsonProperty("properties") val properties: ApiCrimeProperties,
        @JsonProperty("geometry") val geometry: ApiCrimeGeometry)

    private data class ApiResponse(@JsonProperty("features") val crimes: Array<ApiCrime>)

    fun fetchCrimes(): Single<List<Crime>>
    {
        log.debug("Fetching crimes from url: ${url}")
        return Fuel.get(url)
            .rx_responseString()
            .map {
                it.second.fold({ value ->
                    mapper
                        .readValue(value, ApiResponse::class.java)
                        .crimes
                        .map {
                            val props = it.properties;
                            val coords = it.geometry.coords;
                            if (coords.size != 2) null
                            else Crime(
                                date = ZonedDateTime.parse(props.date).toLocalDate(),
                                latitude = coords[1],
                                longitude = coords[0],
                                location = props.location,
                                type = props.type)
                        }
                        .filterNotNull()
                }, { err ->
                    log.warn("Failed to retrieve data", err)
                    throw err
                })
            }
    }

}
