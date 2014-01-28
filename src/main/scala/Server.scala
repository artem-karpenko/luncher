import java.io.{StringWriter, PrintWriter}
import java.sql.DriverManager
import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}
import unfiltered.request.{Seg, Path}
import unfiltered.response.ResponseString
import org.squeryl.PrimitiveTypeMode._

/**
 * @author artem
 */
object Server {
  def main(args: Array[String]) {
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory =
      Some(() => Session.create(DriverManager.getConnection("jdbc:h2:d:/tmp/luncher"), new H2Adapter()))

    val echo = unfiltered.filter.Planify {
      case Path(Seg(p :: Nil)) =>
        val sw: StringWriter = new StringWriter()
        transaction {
          Luncher.printDdl(new PrintWriter(sw))
        }
        ResponseString(sw.toString)
    }

    unfiltered.jetty.Http.anylocal.filter(echo).run()
  }
}
