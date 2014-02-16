package ua.org.yozh

import java.util.Date
import org.squeryl.KeyedEntity
import spray.json._
import org.squeryl.PrimitiveTypeMode._
import scala.collection.mutable.ArrayBuffer

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

  def addOrUpdateAll(userId: Long, orders: Seq[Order]) {
    for (order <- orders) {
      var existingOrder = Luncher.orders.where(o => o.userId === userId and o.date === order.date)
      if (existingOrder.size > 0) {
        Luncher.orders.update(o =>
          where(o.date === existingOrder.head.date)
          set(o.description := order.description))
      } else {
        Luncher.orders.insert(Order(order.description, userId, order.date))
      }
    }
  }

  def deleteByUserIdAndDate(userId: Long, date: Date) = {
    Luncher.orders.deleteWhere(o => o.userId === userId and o.date === date)
  }

  def byUserIdAndDates(userId: Long, from: Date, to: Date) = {
    Luncher.orders.where(o => o.userId === userId and o.date >= from and o.date <= to)
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
      "date" -> o.date.toJson
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("description", "date") match {
        case Seq(JsString(description), JsString(date)) =>
          new Order(description, -1, new Date(BigInt(date).longValue()))
        case _ => throw new DeserializationException("Order expected")
      }
    }
  }

  implicit object OrderListJsonFormat extends RootJsonFormat[Seq[Order]] {
    def write(orders: Seq[Order]): JsValue = {
      val jsObjects = new ArrayBuffer[JsValue]()
      for (o <- orders) {
        jsObjects += o.toJson
      }
      JsArray(jsObjects.toList)
    }

    def read(json: JsValue): Seq[Order] = {
      json match {
        case JsArray(elements) => elements.map(_.convertTo[Order])
        case _ => throw new DeserializationException("Array of Orders expected")
      }
    }
  }
}