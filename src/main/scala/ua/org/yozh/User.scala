package ua.org.yozh

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import spray.json.DefaultJsonProtocol

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
object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat(User.apply, "name", "email")
}
