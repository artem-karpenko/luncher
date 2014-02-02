package ua.org.yozh

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

object Luncher extends Schema {
  val users = table[User]
  val orders = table[Order]("ORDERS")

  on(users)(u => declare(
    u.email is(unique)
  ))

  on(orders)(o => declare(
    o.userId is(indexed)
  ))
}
