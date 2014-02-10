package ua.org.yozh

import java.util.Date
import org.squeryl.KeyedEntity
import spray.json._
import org.squeryl.PrimitiveTypeMode._

/**
 * @author artem
 */
case class Order(
            val description: String,
            val userId: Long,
            val date: Date)  extends KeyedEntity[Long] {
  val id: Long = 0
}

object Order {
  def byUserId(userId: Long) = {
//    val user = User.getByEmail(email).headOption
//    if (user.isDefined) {
      Luncher.orders.where(o => o.userId === userId)
//    } else {
//      None
//    }
  }

  def deleteByUserIdAndDate(userId: Long, date: Date) = {
    Luncher.orders.deleteWhere(o => o.userId === userId and o.date === date)
  }
}

object DateJsonProtocol extends DefaultJsonProtocol {
  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    def write(d: Date) = JsNumber(d.getTime)

    def read(value: JsValue) = {
      new Date(BigInt(value.toString()).longValue())
    }
  }
}

/**
 * For JSON serialization
 */
object OrderJsonProtocol extends DefaultJsonProtocol {
  import DateJsonProtocol._

  implicit object OrderJsonFormat extends RootJsonFormat[Order] {
    def write(o: Order) = JsObject(
      "description" -> JsString(o.description),
//      "userId" -> JsNumber(o.userId),
      "date" -> o.date.toJson
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("description"/*, "userId"*/, "date") match {
        case Seq(JsString(description), JsString(date)) =>
          new Order(description, -1, new Date(BigInt(date).longValue()))
        case _ => throw new DeserializationException("Order expected")
      }
    }
  }
}