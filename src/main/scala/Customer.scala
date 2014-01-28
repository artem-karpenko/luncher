import java.util.Date
import org.squeryl.Schema

/**
 * @author artem
 */
class Customer(val id: Long,
               val firstName: String,
               val lastName: String,
               val email: String) {
}

class Order(val id: Long,
            val order: String,
            val customerId: Long,
            val date: Date) {
}

object Luncher extends Schema {
  val customers = table[Customer]
  val orders = table[Order]
}