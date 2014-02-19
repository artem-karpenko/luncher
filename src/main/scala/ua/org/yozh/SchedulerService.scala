package ua.org.yozh

import org.quartz.impl.StdSchedulerFactory
import java.util.{Calendar, Date}
import org.quartz.{CronScheduleBuilder, SimpleScheduleBuilder, TriggerBuilder, JobBuilder}

/**
 * @author artem
 */
object SchedulerService {
  val scheduler = StdSchedulerFactory.getDefaultScheduler
  scheduler.start()

  /**
   * Regular check for updated orders for this week (or for next week in case it's Sat/Sun)
   */
  def scheduleRegularUpdate() {
    val job = JobBuilder.newJob(classOf[UpdateMailOrderJob]).withIdentity("regularUpdate", "mail").build()
    val trigger = TriggerBuilder.newTrigger()
      .withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(17, 0, 1, 2, 3, 4, 5, 7))
      .startNow()
      .build()
    scheduler.scheduleJob(job, trigger)
  }

  def shutdown() {
    scheduler.shutdown()
  }
}
