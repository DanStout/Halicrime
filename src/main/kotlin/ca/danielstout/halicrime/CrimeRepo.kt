package ca.danielstout.halicrime

import org.sql2o.Sql2o
import java.time.ZonedDateTime
import java.util.*

class CrimeRepo(val sql2o: Sql2o)
{
    val log by logger();

    fun storeCrimes(crimes: List<Crime>): Int
    {
        val sql = """
INSERT INTO crimes (committed_at, latitude, longitude, location, type)
VALUES (:date, :latitude, :longitude, :location, :type)
ON CONFLICT DO NOTHING
"""

        sql2o.beginTransaction().use {
            val query = it.createQuery(sql)
            for (crime in crimes)
            {
                query.bind(crime).addToBatch()
            }
            val result = query.executeBatch().batchResult.toMutableList();
            it.commit();
            val added = Collections.frequency(result, 1);
            log.debug("Attempted to add ${crimes.size} crimes; ${added} were unique")
            return added;
        }
    }

    fun getCrimes(): List<Crime>
    {
        log.debug("Fetching crimes")
        val sql = """
SELECT committed_at date, latitude, longitude, location, type
FROM crimes
ORDER BY committed_at
"""
        sql2o.open().use {
            val crimes = it.createQuery(sql).executeAndFetch(Crime::class.java)
            log.debug("Found ${crimes.size} crimes")
            return crimes
        }
    }


    data class CrimeFetchResult(val time: ZonedDateTime, val success: Boolean, val added: Int)

    fun updateCrimeHistory(result: CrimeFetchResult)
    {
        val sql = """
INSERT INTO crime_fetch_history (fetched_at, success, crimes_added)
VALUES (:time, :success, :added)
"""
        sql2o.open().use {
            it.createQuery(sql)
                .bind(result)
                .executeUpdate()
        }
    }

    fun getLastCrimeFetch(): CrimeFetchResult?
    {
        val sql = "SELECT fetched_at AS time, success, crimes_added AS added FROM crime_fetch_history ORDER BY fetched_at DESC LIMIT 1";
        sql2o.open().use {
            return it.createQuery(sql).executeAndFetchFirst(CrimeFetchResult::class.java)
        }
    }
}