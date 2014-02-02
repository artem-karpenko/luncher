package ua.org.yozh

import java.util.Date

/**
 * @author artem
 */
case class Order(val id: Long,
            val description: String,
            val userId: Long,
            val date: Date) {
}
