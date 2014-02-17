package ua.org.yozh

import unfiltered.request._
import java.io.{PrintWriter, StringWriter}
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import unfiltered.response.{Ok, NotFound, ResponseString}
import org.squeryl.{Query, Session, SessionFactory}
import java.sql.DriverManager
import org.squeryl.adapters.H2Adapter
import spray.json._
import java.util.Date

//import DefaultJsonProtocol._
//import UserJsonProtocol._
//import OrderJsonProtocol._
import scala.Some

/**
 * @author artem
 */
object Server {
  def main(args: Array[String]) {
    initSessionFactory()

//    transaction {
//      Luncher.create
//    }
    MailService.sendMail(new Date(), new Date(2014, 1, 30))

    val echo = unfiltered.filter.Planify {
      case GET(Path("/users")) =>
        import UserJsonProtocol._

        transaction {
          val usersQuery: Query[User] = User.allUsers
          ResponseString(usersQuery.asInstanceOf[Iterable[User]].toJson.prettyPrint)
        }

      case GET(Path(Seg("users" :: userEmail :: Nil))) =>
        import UserJsonProtocol._

        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            ResponseString(user.get.toJson.prettyPrint)
          } else {
            NotFound
          }
        }

      case GET(Path(Seg("users" :: userEmail :: "orders" :: Nil))) =>
        import OrderJsonProtocol._

        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            val orders = Order.byUserId(user.get.id)
            ResponseString(orders.asInstanceOf[Iterable[Order]].toJson.prettyPrint)
          } else {
            NotFound
          }
        }

      case GET(Path(Seg("users" :: userEmail :: "orders" :: "from" :: fromDate :: "to" :: toDate :: Nil))) =>
        import OrderJsonProtocol._

        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            val orders = Order.byUserIdAndDates(user.get.id, new Date(BigInt(fromDate).longValue()),
              new Date(BigInt(toDate).longValue()))
            ResponseString(orders.asInstanceOf[Iterable[Order]].toJson.prettyPrint)
          } else {
            NotFound
          }
        }

      case req @ POST(Path("/users")) =>
        import UserJsonProtocol._

        val bytes = Body.bytes(req)
        val user = new String(bytes).asJson.convertTo[User]
        transaction {
          Luncher.users.insert(user)
        }
        ResponseString("USER ADDED")

      case req @ POST(Path(Seg("users" :: userEmail :: "orders" :: Nil))) =>
        import OrderJsonProtocol._

        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            val bytes = Body.bytes(req)
            val order = new String(bytes).asJson.convertTo[Order]
            transaction {
              Luncher.orders.insert(Order(order.description, user.get.id, order.date))
            }
            ResponseString("ORDER ADDED")
          } else {
            NotFound
          }
        }

      case req @ POST(Path(Seg("users" :: userEmail :: "orders" :: "all" :: Nil))) =>
        import OrderListJsonProtocol._

        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            val bytes = Body.bytes(req)
            val orders = new String(bytes).asJson.convertTo[Seq[Order]]
            transaction {
              Order.addOrUpdateAll(user.get.id, orders)
            }
            ResponseString("ORDERS SAVED")
          } else {
            NotFound
          }
        }

      case DELETE(Path(Seg("users" :: "deleteByEmail" :: email :: Nil))) =>
        transaction {
          val user = User.getByEmail(email).headOption

          if (user.isDefined) {
            Order.deleteByUserId(user.get.id)
          }

          val deletedCount = User.deleteByEmail(email)
          if (deletedCount != 0) {
            ResponseString("USER REMOVED")
          } else {
            NotFound
          }
        }

      case DELETE(Path(Seg("users" :: userEmail :: "orders" :: date :: Nil))) =>
        transaction {
          val user = User.getByEmail(userEmail).headOption
          if (user.isDefined) {
            val deletedCount = Order.deleteByUserIdAndDate(user.get.id, new Date(BigInt(date).longValue()))
            if (deletedCount != 0) {
              ResponseString("ORDER REMOVED")
            } else {
              NotFound
            }
          } else {
            NotFound
          }
        }

      case POST(Path(Seg("/sendMail" :: "from" :: fromDate :: "to" :: toDate :: Nil))) =>
        transaction {
          MailService.sendMail(new Date(BigInt(fromDate).longValue),
            new Date(BigInt(toDate).longValue))
          Ok
        }
    }

    unfiltered.jetty.Http.local(8080).filter(echo).run()
  }

  private def initSessionFactory() {
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory =
      Some(() => Session.create(DriverManager.getConnection("jdbc:h2:d:/tmp/luncher"), new H2Adapter()))
  }
}
