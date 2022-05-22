package shop.routes

import cats._
import org.http4s._
import org.http4s.circe.{ JsonDecoder, toMessageSyntax }
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import shop.domain.auth.{ PasswordParam, User, UsernameParam }

import java.time.Year

final case class AuthRoutes[F[_]: Defer: JsonDecoder: Monad]() extends Http4sDsl[F] {

  private val login: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case POST -> Root / "login" => Ok("login")
    }
  }

  private val signUp: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "signup" :? UsernameParam(username) +& PasswordParam(password) =>
        Ok(s"login $username,$password")
    }
  }

  val routes: HttpRoutes[F] = Router("auth" -> login, "auth" -> signUp)

}
