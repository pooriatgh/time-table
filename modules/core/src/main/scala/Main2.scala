import cats.effect._
import cats.effect.std.Supervisor
import dev.profunktor.redis4cats.log4cats._
import eu.timepit.refined.auto._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import shop.modules._
import shop.resources._

object Main2 extends IOApp.Simple {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = Supervisor[IO].use { implicit sp =>
    AppResources
      .make[IO]
      .evalMap { res =>
        Security.make[IO](res.redis).map { security =>
          val api = HttpApi.make[IO](security)
          "0.0.0.0" -> api.httpApp
        }
      }
      .flatMap {
        case (cfg, httpApp) =>
          MkHttpServer[IO].newEmber(cfg, httpApp)
      }
      .useForever

  }

}
