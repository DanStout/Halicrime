package ca.danielstout.halicrime

import java.time.*
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Scheduler
{
    private val log by logger()
    private val exec = Executors.newScheduledThreadPool(1)

    fun scheduleTaskRepeatingWeekly(task: () -> Unit, time: LocalTime, day: DayOfWeek)
    {
        val run = {
            log.info("Invoking task")
            task.invoke()
            scheduleTaskRepeatingWeekly(task, time, day)
        }

        val today = LocalDate.now()
        var then = today.with(TemporalAdjusters.nextOrSame(day)).atTime(time)
        val now = LocalDateTime.now();
        if (now.isEqual(then) || now.isAfter(then))
        {
            log.debug("Scheduled time right now or in the past; adjusting")
            then = today.with(TemporalAdjusters.next(day)).atTime(time)
        }
        val delay = getSecondsUntil(then)
        log.debug("Scheduled task at ${then} which is in ${delay} seconds")
        exec.schedule(run, delay, TimeUnit.SECONDS)
    }

    private fun getSecondsUntil(time: LocalDateTime): Long
    {
        val now = LocalDateTime.now()
        return if (now.isAfter(time)) 0 else Duration.between(now, time).seconds
    }

}

