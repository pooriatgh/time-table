package shop.routes

import cats._
import cats.syntax.all._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import shop.domain.auth.{ Password, PasswordParam, Username, UsernameParam }
import shop.services.Auth

final case class AuthRoutes[F[_]: Defer: JsonDecoder: Monad](auth: Auth[F]) extends Http4sDsl[F] {

  private val login: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case POST -> Root / "login" :? UsernameParam(username) +& PasswordParam(password) =>
        auth.login(Username(username), Password(password)).flatMap(p => Ok(p.value))
    }
  }

  private val signUp: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case POST -> Root / "signup" :? UsernameParam(username) +& PasswordParam(password) =>
        auth.newUser(Username(username), Password(password)).flatMap(p => Created(p.value))
    }
  }

  val routes: HttpRoutes[F] = Router("auth" -> login, "auth" -> signUp)

}
