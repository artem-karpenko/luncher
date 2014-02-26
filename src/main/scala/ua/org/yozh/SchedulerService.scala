package ua.org.yozh

import org.quartz.impl.StdSchedulerFactory
import java.util.{Calendar, Date}
import org.quartz._
import org.joda.time.{Interval, DateTimeConstants, DateTime}
import DateTimeConstants._
import scala.Some
import ua.org.yozh.jobs.UpdateMailOrderJob

/**
 * @author artem
 */
object SchedulerService {
  val scheduler = StdSchedulerFactory.getDefaultScheduler
  scheduler.start()

  /**
   * Regular check for updated orders for this week (or for next week in case it's Fri=Sun)
   */
  def scheduleRegularUpdate() {
    val job = JobBuilder.newJob(classOf[UpdateMailOrderJob]).withIdentity("regularUpdate", "mail").build()
    val trigger = TriggerBuilder.newTrigger()
      .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(17, 0))
//      .withSchedule(CronScheduleBuilder.cronSchedule("30 * * * * ?"))
      .startNow()
      .build()
    scheduler.scheduleJob(job, trigger)
  }

  /**
   * Checks running each Friday hourly from 11 till 16 o'clock to check for unsaved orders
   * for the next week.
   */
  def scheduleFridayChecks() {
    val job = JobBuilder.newJob(classOf[jobs.FridayChecksJob]).withIdentity("fridayChecks", "mail").build()
    val trigger = TriggerBuilder.newTrigger()
      .withSchedule(CronScheduleBuilder.cronSchedule("0 0 11-16 ? * FRI"))
      .startNow()
      .build()
    scheduler.scheduleJob(job, trigger)
  }

  /**
   * Build interval for this week (if it's Mon-Thu) or for the next week (if it's Fri-Sun).
   * This is the interval that will be used by [[jobs.UpdateMailOrderJob]]
   */
  def getThisOrNextWeekBounds = {
    val today = new DateTime()
    if (today.getDayOfWeek < FRIDAY) {
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
