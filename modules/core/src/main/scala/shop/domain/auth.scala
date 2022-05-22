package shop.domain

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import skunk.codec.`enum`

import java.util.UUID

object auth {

  case class User(userId: UUID, username: String, password: String)

  sealed trait Role
  case object Admin  extends Role
  case object Common extends Role
  case object Guest  extends Role

  object UsernameParam extends QueryParamDecoderMatcher[String]("username")
  object PasswordParam extends QueryParamDecoderMatcher[String]("password")

}
