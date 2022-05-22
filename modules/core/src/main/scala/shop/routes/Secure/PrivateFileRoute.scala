package shop.routes.Secure

import shop.http.auth.users.CommonUser
import shop.http.vars.OrderIdVar
import shop.services.Orders
import cats._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.domain.auth.User

final case class OrderRoutes[F[_]: Defer: Monad]() extends Http4sDsl[F] {

  private[routes] val prefixPath = "/orders"

  private val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root as user => Ok(user.username)
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
