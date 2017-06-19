package ca.danielstout.halicrime

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.IOException

class Config(val dbUser: String, val dbPass: String, val dbPort: Int, val dbHost: String, val dbName: String)
{
    companion object
    {
        val log by logger()
        val file = File("config.json")

        fun load(mapper: ObjectMapper): Config
        {
            try
            {
                return mapper.readValue(file)
            }
            catch(ex: IOException)
            {
                log.error("Failed to read {}", file.absolutePath)
                throw ex
            }
        }
    }
}