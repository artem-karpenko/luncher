package ua.org.yozh

import org.quartz.{JobExecutionContext, Job}

/**
* Checks for "updated orders" flag and if it's set - sends email
* @author artem
*/
class UpdateMailOrderJob extends Job {
  override def execute(context: JobExecutionContext) {
    if (Settings.ordersUpdated) {

      Settings.ordersUpdated = false
    }
  }
}
