package ua.org.yozh

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import ua.org.yozh.entity.{User, Order}

object Luncher extends Schema {
  val users = table[User]
  val orders = table[Order]("ORDERS")

  on(users)(u => declare(
    u.email is(unique, indexed)
  ))

  on(orders)(o => declare(
    o.userId is(indexed)
  ))
}
