package shop.routes

import cats.implicits.toSemigroupKOps
import cats.{ Defer, Monad }
import org.http4s.circe.JsonDecoder
import org.http4s.implicits._
import org.http4s.server.AuthMiddleware
import org.http4s.{ HttpApp, HttpRoutes }
import shop.domain.auth.User
import shop.routes.Secure.PrivateFileRoute
import shop.services.Auth

case class AllRoutes[F[_]: Monad: Defer: JsonDecoder](middleware: AuthMiddleware[F, User], auth: Auth[F]) {

  def get: HttpRoutes[F] =
    AuthRoutes[F](auth).routes <+> FileRoutes[F]().routes <+> PrivateFileRoute[F]().routes(middleware)

  def getComplete: HttpApp[F] = get.orNotFound
}
