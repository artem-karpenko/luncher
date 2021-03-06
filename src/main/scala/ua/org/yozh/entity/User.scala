package ua.org.yozh.entity

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import spray.json._
import ua.org.yozh.Luncher

/**
 * @author artem
 */
case class User(val name: String,
                val email: String) extends KeyedEntity[Long] {
  val id: Long = 0
}

object User {
  def allUsers = {
    from(Luncher.users)(s => select(s))
  }

  def getByEmail(email: String) = {
    Luncher.users.where(u => u.email === email)
  }

  def deleteByEmail(email: String) = {
    Luncher.users.deleteWhere(u => u.email === email)
  }
}

/**
 * For JSON serialization
 */
//object UserJsonProtocol extends DefaultJsonProtocol {
//  implicit val userFormat = jsonFormat(User.apply, "name", "email")
//}

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit object UserJsonFormat extends RootJsonFormat[User] {
    def write(u: User) = JsObject(
      "name" -> JsString(u.name),
      "email" -> JsString(u.email)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("name", "email") match {
        case Seq(JsString(name), JsString(email)) =>
          new User(name, email)
        case _ => throw new DeserializationException("Order expected")
      }
    }
  }
}