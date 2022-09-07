package shop.modules

import cats.effect.Sync
import cats.implicits.catsSyntaxApplicativeId
import dev.profunktor.auth.jwt.{ JwtAuth, JwtSymmetricAuth }
import dev.profunktor.redis4cats.RedisCommands
import pdi.jwt.JwtAlgorithm
import shop.domain.auth.User
import shop.services.{ Auth, Users, UsersAuth }

object Security {
  def make[F[_]: Sync](redis: RedisCommands[F, String, String]): F[Security[F]] = {
    val users     = Users.make[F]()
    val tokens    = Tokens.make[F]
    val auth      = Auth.make[F](users, tokens, redis)
    val usersAuth = UsersAuth.common[F](redis)
    val userJwtAuth = JwtAuth.hmac(
      "53cr3t",
      JwtAlgorithm.HS256
    )
    new Security[F](auth, usersAuth, userJwtAuth) {}.pure[F]
  }

}

sealed abstract class Security[F[_]] private (
    val auth: Auth[F],
    val usersAuth: UsersAuth[F, User],
    val userJwtAuth: JwtSymmetricAuth
)
