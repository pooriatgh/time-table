package shop.routes.Secure

import cats._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.domain.auth.User

final case class PrivateFileRoute[F[_]: Defer: Monad]() extends Http4sDsl[F] {

  private[routes] val prefixPath = "/privateFile"

  private val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root as user => Ok(user.username.value)
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
