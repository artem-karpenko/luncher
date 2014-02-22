package ua.org.yozh

import org.quartz.impl.StdSchedulerFactory
import java.util.{Calendar, Date}
import org.quartz._
import org.joda.time.{Interval, DateTimeConstants, DateTime}
import DateTimeConstants._
import scala.Some

/**
 * @author artem
 */
object SchedulerService {
  val scheduler = StdSchedulerFactory.getDefaultScheduler
  scheduler.start()

  /**
   * Regular check for updated orders for this week (or for next week in case it's Sat/Sun)
   * Scheduled at 17:00 every day except Friday
   */
  def scheduleRegularUpdate() {
    val job = JobBuilder.newJob(classOf[UpdateMailOrderJob]).withIdentity("regularUpdate", "mail").build()
    val trigger = TriggerBuilder.newTrigger()
      .withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(17, 0, DateBuilder.MONDAY,
        DateBuilder.TUESDAY, DateBuilder.WEDNESDAY, DateBuilder.THURSDAY, DateBuilder.SATURDAY,
        DateBuilder.SUNDAY))
//      .withSchedule(CronScheduleBuilder.cronSchedule("30 * * * * ?"))
      .startNow()
      .build()
    scheduler.scheduleJob(job, trigger)
  }

  /**
   * Build interval for this week (if it's Mon-Fri)
   * or for the next week (if it's Sat-Sun).
   * This is the interval that will be used by [[ua.org.yozh.UpdateMailOrderJob]]
   */
  def getThisOrNextWeekBounds = {
    val today = new DateTime()
    if (today.getDayOfWeek < SATURDAY) {
      val nextFriday = today.withDayOfWeek(FRIDAY)
      new Interval(today, nextFriday)
    } else {
      new Interval(today.plusWeeks(1).withDayOfWeek(MONDAY),
        today.plusWeeks(1).withDayOfWeek(FRIDAY))
    }
  }

  def shutdown() {
    scheduler.shutdown()
  }
}
