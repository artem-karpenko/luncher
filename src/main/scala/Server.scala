import unfiltered.request.{Seg, Path}
import unfiltered.response.ResponseString

/**
 * @author artem
 */
object Server {
  def main(args: Array[String]) {
    val echo = unfiltered.filter.Planify {
      case Path(Seg(p :: Nil)) => ResponseString(p)
    }

    unfiltered.jetty.Http.anylocal.filter(echo).run()
  }
}
