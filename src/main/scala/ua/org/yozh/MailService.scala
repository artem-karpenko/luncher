package ua.org.yozh

import java.util.{Formatter, Properties, Date}
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import org.squeryl.PrimitiveTypeMode._
import java.text.SimpleDateFormat
import org.squeryl.{PrimitiveTypeMode, Query}
import org.squeryl.dsl.GroupWithMeasures
import scala.collection.mutable.MapLike
import scala.collection.mutable
import ua.org.yozh.entity.Order

/**
 * Service responsible for building and sending of order email
 * @author artem
 */
object MailService {
  val dateFormat = new SimpleDateFormat("dd.MM")

  /**
   * Send email with orders for given date bounds
   * @param from
   * @param to
   */
  def sendMail(from: Date, to: Date) {
    val text = new StringBuilder
    val orderMap = new collection.mutable.LinkedHashMap[Date, collection.mutable.Map[String, Long]]

    // collect orders into a map
    transaction {
      val orders = Order.groupedByDayAndDesc(from, to)
      for (order <- orders) {
        if (!orderMap.contains(order.key._1)) {
          orderMap(order.key._1) = new mutable.HashMap[String, Long]
        }
        orderMap(order.key._1) += (order.key._2.get -> order.measures)
      }
    }

    // build email text
    orderMap foreach { day =>
      text.append(dateFormat.format(day._1)).append('\n')
      day._2.foreach { order =>
        text.append(order._1).append(" - ").append(order._2).append('\n')
      }
      text.append('\n')
    }

//    println(text)

    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "587")

    val session = Session.getInstance(props,
      new javax.mail.Authenticator {
        override def getPasswordAuthentication = {
          new PasswordAuthentication(System.getProperty("username"), System.getProperty("password"))
        }
      }
    )

    // final settings and send
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress("food-orders@smiss.ua"))
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(System.getProperty("username")))
    message.setSubject(new Formatter().format("Order for %1$td.%1$tm - %2$td.%2$tm", from, to).toString)
    message.setSentDate(new Date)
    message.setText(text.toString())

    Transport.send(message)
  }
}
