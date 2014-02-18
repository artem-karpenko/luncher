package ua.org.yozh.entity

import java.util.Date
import org.squeryl.KeyedEntity
import spray.json._
import org.squeryl.PrimitiveTypeMode._
import scala.collection.mutable.ArrayBuffer
import ua.org.yozh.Luncher

/**
 * @author artem
 */
case class Order(
            val description: Option[String],
            val userId: Long,
            val date: Date)  extends KeyedEntity[Long] {
  val id: Long = 0
}

object Order {
  def byUserId(userId: Long) = {
      Luncher.orders.where(o => o.userId === userId)
  }

  def addOrUpdateAll(userId: Long, orders: Seq[Order]) {
    for (order <- orders) {
      var existingOrder = Luncher.orders.where(o => o.userId === userId and o.date === order.date)
      if (existingOrder.size > 0) {
        Luncher.orders.update(o =>
          where(o.date === existingOrder.head.date and o.userId === userId)
          set(o.description := order.description))
      } else {
        Luncher.orders.insert(Order(order.description, userId, order.date))
      }
    }
  }

  def deleteByUserId(userId: Long) = {
    Luncher.orders.deleteWhere(o => o.userId === userId)
  }

  def deleteByUserIdAndDate(userId: Long, date: Date) = {
    Luncher.orders.deleteWhere(o => o.userId === userId and o.date === date)
  }

  def byUserIdAndDates(userId: Long, from: Date, to: Date) = {
    Luncher.orders.where(o => o.userId === userId and o.date >= from and o.date <= to)
  }

  def groupedByDayAndDesc(fromDate: Date, toDate: Date) = {
    import scala.language.postfixOps

    from(Luncher.orders)(o =>
      where(o.date >= fromDate and o.date <= toDate and (o.description isNotNull))
      groupBy(o.date, o.description)
      compute(count())
      orderBy(o.date)
    )
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
    def write(o: Order) = {
      JsObject(
        "description" -> (if (o.description.nonEmpty) JsString(o.description.get) else JsNull),
        "date" -> o.date.toJson
      )
    }

    def read(value: JsValue) = {
      value.asJsObject.getFields("description", "date") match {
        case Seq(JsString(description), JsNumber(date)) =>
          new Order(Option(description), -1, new Date(date.longValue()))

        case Seq(JsNull, JsNumber(date)) =>
          new Order(None, -1, new Date(date.longValue()))

        case _ => throw new DeserializationException("Order expected")
      }
    }
  }
}

object OrderListJsonProtocol extends DefaultJsonProtocol {
  import OrderJsonProtocol._

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