package shop.routes

import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

case class FileRoutes[F[_]: Monad]() extends Http4sDsl[F] {

  private val fileRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root          => ???
    case GET -> Root / "half" => ???
    case GET -> Root / "full" => ???
  }

  val routes: HttpRoutes[F] = Router("file" -> fileRoutes)
}
