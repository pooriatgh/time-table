package shop.domain

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import scala.util.control.NoStackTrace

object auth {

  @derive(decoder, encoder)
  case class User(userId: Long, username: Username, password: Password, role: Role = Guest)

  @derive(decoder, encoder, eqv, show)
  case class Username(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class Password(value: String)

  @derive(decoder, encoder)
  sealed trait Role
  case object Admin  extends Role
  case object Common extends Role
  case object Guest  extends Role

  object UsernameParam extends QueryParamDecoderMatcher[String]("username")
  object PasswordParam extends QueryParamDecoderMatcher[String]("password")

  case class UserNameInUse(username: Username)   extends NoStackTrace
  case class UserNotFound(username: Username)    extends NoStackTrace
  case class InvalidPassword(username: Username) extends NoStackTrace

}
