package ua.org.yozh

import java.util.Properties
import java.io.{FileOutputStream, FileInputStream, File}
import org.slf4j.{LoggerFactory, Logger}

/**
 * @author artem
 */
object Settings {
  val LOG = LoggerFactory.getLogger(Settings.getClass)

  val properties = new Properties()

  val settingsFile = {
    val settingsFileLocation = System.getProperty("settingsFile")
    if (settingsFileLocation != null) {
      Some(new File(settingsFileLocation))
    } else {
      None
    }
  }

  // load settings
  if (settingsFile.isDefined) {
    if (settingsFile.get.exists()) {
      LOG.info("Loading luncher settings from " + settingsFile.get.getAbsolutePath)
      val is: FileInputStream = new FileInputStream(settingsFile.get)
      properties.load(is)
      is.close()
    } else {
      LOG.warn(settingsFile.get.getAbsolutePath + " does not exists, will use defaults")
    }
  } else {
    LOG.warn("No settings file defined, will use defaults")
  }

  // use defaults if necessary
  if (properties.getProperty("regular.ordersUpdated") == null) {
    properties.setProperty("regular.ordersUpdated", "false")
  }

  def ordersUpdated = properties.getProperty("regular.ordersUpdated").toBoolean
  def ordersUpdated_=(updated: Boolean) {
    properties.setProperty("regular.ordersUpdated", updated.toString)
  }

  /**
   * Save settings into properties files
   */
  def save() {
    if (settingsFile.isDefined) {
      LOG.info("Saving luncher settings to " + settingsFile.get.getAbsolutePath)
      val os: FileOutputStream = new FileOutputStream(settingsFile.get)
      properties.store(os, null)
      os.close()
      LOG.info("Luncher settings saved")
    } else {
      LOG.warn("No settings files defined, luncher settings cannot be saved")
    }
  }
}
