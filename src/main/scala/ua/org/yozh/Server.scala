package ua.org.yozh

import unfiltered.request._
import java.io.{PrintWriter, StringWriter}
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import unfiltered.response.ResponseString
import org.squeryl.{Query, Session, SessionFactory}
import java.sql.DriverManager
import org.squeryl.adapters.H2Adapter
import spray.json._
import UserJsonProtocol._
import scala.Some
import unfiltered.response.ResponseString

//import DefaultJsonProtocol._
import scala.collection.mutable.ArrayBuffer
;

/**
 * @author artem
 */
object Server {
  def main(args: Array[String]) {
    initSessionFactory()

    val echo = unfiltered.filter.Planify {
      case GET(Path("/users")) =>
        transaction {
          val usersQuery: Query[User] = User.allUsers
          ResponseString(usersQuery.asInstanceOf[Iterable[User]].toJson.prettyPrint)
        }

      case req @ PUT(Path("/users/add")) =>
        val bytes = Body.bytes(req)
        val user = new String(bytes).asJson.convertTo[User]
        transaction {
          Luncher.users.insert(user)
        }
        ResponseString("USER ADDED")

      case DELETE(Path(Seg("users" :: "deleteByEmail" :: email :: Nil))) =>
        transaction {
          val deletedCount = User.deleteByEmail(email)
          var response = "USER REMOVED"
          if (deletedCount == 0) {
            response = "USER NOT FOUND"
          }
          ResponseString(response)
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
