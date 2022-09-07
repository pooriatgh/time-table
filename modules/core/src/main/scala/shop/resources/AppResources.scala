package shop.resources

import cats.effect.std.Console
import cats.effect.{ Concurrent, Resource }
import cats.syntax.all._
import dev.profunktor.redis4cats.effect.MkRedis
import dev.profunktor.redis4cats.{ Redis, RedisCommands }
import eu.timepit.refined.auto._
import fs2.io.net.Network
import org.typelevel.log4cats.Logger
import skunk._
import skunk.codec.text._
import skunk.implicits._

sealed abstract class AppResources[F[_]](
    val redis: RedisCommands[F, String, String]
)

object AppResources {

  def make[F[_]: Concurrent: Console: Logger: MkRedis: Network]: Resource[F, AppResources[F]] = {

    def checkRedisConnection(
        redis: RedisCommands[F, String, String]
    ): F[Unit] =
      redis.info.flatMap {
        _.get("redis_version").traverse_ { v =>
          Logger[F].info(s"Connected to Redis $v")
        }
      }

    def mkRedisResource: Resource[F, RedisCommands[F, String, String]] =
      Redis[F].utf8("redis://localhost").evalTap(checkRedisConnection)

    (mkRedisResource).map(new AppResources[F](_) {})

  }

}
