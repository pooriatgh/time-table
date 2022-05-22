package shop.routes

import cats.implicits.toSemigroupKOps
import cats.{ Defer, Monad }
import org.http4s.{ HttpApp, HttpRoutes }
import org.http4s.circe.JsonDecoder
import org.http4s.implicits._

object allRoutes {
  def get[F[_]: Monad: Defer: JsonDecoder]: HttpRoutes[F]      = AuthRoutes[F]().routes <+> FileRoutes[F]().routes
  def getComplete[F[_]: Monad: Defer: JsonDecoder]: HttpApp[F] = get[F].orNotFound
}
