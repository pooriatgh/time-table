package shop.services

import cats.effect._
import cats.implicits.catsSyntaxApplicativeId
import cats.syntax.all._
import shop.domain.auth.{ Password, User, Username }

trait Users[F[_]] {
  def find(username: Username): F[Option[User]]
  def create(username: Username, password: Password): F[Long]
}

object Users {

  var db: List[User] = List()

  def make[F[_]: MonadCancelThrow](): Users[F] = new Users[F] {
    override def find(username: Username): F[Option[User]] = {
      db.find(_.username.value == username.value).pure[F]
    }

    override def create(username: Username, password: Password): F[Long] = {
      val r = scala.util.Random
      db = User(r.nextLong(), username, password) :: db
      db.length.toLong.pure[F]
    }
  }
}
