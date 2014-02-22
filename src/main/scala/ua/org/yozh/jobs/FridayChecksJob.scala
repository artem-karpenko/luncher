package ua.org.yozh.jobs

import org.quartz.{JobExecutionContext, Job}
import org.slf4j.LoggerFactory

/**
 * @author artem
 */
class FridayChecksJob extends Job {
  val LOG = LoggerFactory.getLogger(classOf[FridayChecksJob])

  override def execute(context: JobExecutionContext) {

  }
}
