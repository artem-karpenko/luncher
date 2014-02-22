package ua.org.yozh

import org.quartz.{JobExecutionContext, Job}
import org.slf4j.LoggerFactory

/**
* Checks for "updated orders" flag and if it's set - sends email
* @author artem
*/
class UpdateMailOrderJob extends Job {
  val LOG = LoggerFactory.getLogger(classOf[UpdateMailOrderJob])

  override def execute(context: JobExecutionContext) {
    LOG.info("Checking for updates in orders")
    if (Settings.ordersUpdated) {
      LOG.info("Orders updated, sending mail")

      MailService.sendMail(SchedulerService.getThisOrNextWeekBounds)

      Settings.ordersUpdated = false
    } else {
      LOG.info("Orders were not updated")
    }
  }
}
