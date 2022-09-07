package shop.modules

import cats.Monad
import cats.syntax.all._
import dev.profunktor.auth.jwt.{ JwtAuth, JwtSecretKey, JwtToken, jwtEncode }
import pdi.jwt.{ JwtAlgorithm, JwtClaim }

import java.util.UUID

trait Tokens[F[_]] {
  def create: F[JwtToken]
}

object Tokens {
  //(jwtExpire: JwtExpire[F], config: JwtSecretKeyConfig, exp: TokenExpiration)
  def make[F[_]: Monad]: Tokens[F] = {
    new Tokens[F] {
      override def create: F[JwtToken] = {
        val jwtAuth = JwtAuth.hmac("53cr3t", JwtAlgorithm.HS256)
        val tokenId = UUID.randomUUID()
        jwtEncode[F](JwtClaim(tokenId.toString), JwtSecretKey("53cr3t"), JwtAlgorithm.HS256)
      }
    }
  }
}
