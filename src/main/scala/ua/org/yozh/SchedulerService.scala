package ua.org.yozh

import org.quartz.impl.StdSchedulerFactory
import java.util.{Calendar, Date}
import org.quartz.{SimpleScheduleBuilder, TriggerBuilder, JobBuilder}

/**
 * @author artem
 */
object SchedulerService {
  val scheduler = StdSchedulerFactory.getDefaultScheduler
  scheduler.start()

  def scheduleRegularUpdate() {
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.HOUR, 1)
    val inHour: Date = cal.getTime

    val job = JobBuilder.newJob(classOf[UpdateMailOrderJob]).withIdentity("regularUpdate", "mail").build()
    val trigger = TriggerBuilder.newTrigger()
      .withSchedule(SimpleScheduleBuilder.simpleSchedule())
      .startAt(inHour)
      .build()
    scheduler.scheduleJob(job, trigger)
  }

  def shutdown() {
    scheduler.shutdown()
  }
}
