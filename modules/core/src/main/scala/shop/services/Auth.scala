package shop.services

import cats._
import cats.syntax.all._
import io.circe.parser.decode
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe.syntax.EncoderOps
import pdi.jwt.JwtClaim
import shop.domain.auth._
import shop.modules.Tokens

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

trait Auth[F[_]] {
  def newUser(username: Username, password: Password): F[JwtToken]
  def login(username: Username, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: Username): F[Unit]
}

trait UsersAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}
object UsersAuth {

  def common[F[_]: MonadThrow](
      redis: RedisCommands[F, String, String]
  ): UsersAuth[F, User] =
    new UsersAuth[F, User] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[User]] =
        redis
          .get(token.value)
          .map {
            _.flatMap { u =>
              //User(6, Username("aha"), Password("dd")).some
              decode[User](u).toOption
            }
          }
    }

}

object Auth {
  def make[F[_]: MonadThrow](
      users: Users[F],
      tokens: Tokens[F],
      redis: RedisCommands[F, String, String],
      tokenExpiration: FiniteDuration = 30.minute
  ): Auth[F] = new Auth[F] {
    override def newUser(username: Username, password: Password): F[JwtToken] = users.find(username).flatMap {
      case Some(_) => UserNameInUse(username).raiseError[F, JwtToken]
      case _ =>
        for {
          t   <- tokens.create
          dbR <- users.create(username, password)
          u = User(dbR, username, password, Admin).asJson.noSpaces
          _ <- redis.setEx(t.value, u, tokenExpiration)
          _ <- redis.setEx(username.show, t.value, tokenExpiration)
        } yield t
    }

    override def login(username: Username, password: Password): F[JwtToken] = users.find(username).flatMap {
      case None => UserNotFound(username).raiseError[F, JwtToken]
      case Some(user) if user.password.value =!= password.value =>
        InvalidPassword(user.username).raiseError[F, JwtToken]
      case Some(user) =>
        redis.get(username.show).flatMap {
          case Some(t) => JwtToken(t).pure[F]
          case None =>
            tokens.create.flatTap { t =>
              redis.setEx(t.value, user.toString, tokenExpiration) *>
                redis.setEx(username.show, t.value, tokenExpiration)
            }
        }
    }

    override def logout(token: JwtToken, username: Username): F[Unit] = ???
  }
}
