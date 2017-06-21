package ca.danielstout.halicrime

import org.sql2o.Sql2o
import org.sql2o.converters.Converter
import org.sql2o.converters.ConverterException
import org.sql2o.quirks.PostgresQuirks
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import javax.sql.DataSource


class Sql2oManager
{
    companion object
    {
        fun getSql2o(src: DataSource): Sql2o
        {
            val map = mapOf<Class<*>, Converter<*>>(
                LocalDate::class.java to LocalDateConverter(),
                ZonedDateTime::class.java to ZonedDateTimeConverter())
            val quirks = PostgresQuirks(map)
            return Sql2o(src, quirks)
        }
    }

    private class ZonedDateTimeConverter : Converter<ZonedDateTime?>
    {
        override fun convert(src: Any?): ZonedDateTime?
        {
            return when (src)
            {
                null -> null
                is Date -> ZonedDateTime.ofInstant(src.toInstant(), ZoneId.systemDefault())
                else ->
                {
                    try
                    {
                        return Timestamp.valueOf(src.toString()).toLocalDateTime().atZone(ZoneId.systemDefault());
                    }
                    catch (ex: DateTimeParseException)
                    {
                        val msg = "Unable to convert value $src of type ${src.javaClass.name} to a LocalDate"
                        throw ConverterException(msg)
                    }

                }
            }
        }

        override fun toDatabaseParam(date: ZonedDateTime?): Any?
        {
            return when (date)
            {
                null -> null
                else ->
                {
                    Timestamp.valueOf(date.toLocalDateTime());
                }
            }
        }

    }

    private class LocalDateConverter : Converter<LocalDate?>
    {
        override fun convert(src: Any?): LocalDate?
        {
            return when (src)
            {
                null -> null
                is Date -> src.toLocalDate()
                else ->
                {
                    try
                    {
                        return Timestamp.valueOf(src.toString()).toLocalDateTime().toLocalDate()
                    }
                    catch (ex: DateTimeParseException)
                    {
                        val msg = "Unable to convert value $src of type ${src.javaClass.name} to a LocalDate"
                        throw ConverterException(msg)
                    }

                }
            }
        }

        override fun toDatabaseParam(date: LocalDate?): Any?
        {
            return when (date)
            {
                null -> null
                else ->
                {
                    Timestamp.valueOf(date.atStartOfDay());
                }
            }
        }

    }

}