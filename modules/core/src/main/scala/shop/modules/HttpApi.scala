package shop.modules

import cats.effect.Async
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s.HttpApp
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.{ RequestLogger, ResponseLogger }
import shop.domain.auth.User
import shop.routes.AllRoutes

object HttpApi {
  def make[F[_]: Async](
      security: Security[F]
  ): HttpApi[F] =
    new HttpApi[F](security) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
    security: Security[F]
) {

  private val usersMiddleware: AuthMiddleware[F, User] =
    JwtAuthMiddleware[F, User](security.userJwtAuth, security.usersAuth.findUser)

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(new AllRoutes[F](usersMiddleware, security.auth).getComplete)

}
