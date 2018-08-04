package store.config

import cats.effect.Sync
import pureconfig.{ConfigReader, Derivation}
import pureconfig.error.ConfigReaderFailures

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ConfigLoader[Config] {

  def default[F[_]: Sync]: F[Config]

  def load[F[_]: Sync](implicit reader: Derivation[ConfigReader[Config]]): F[Config] = {
    suspendInF(pureconfig.loadConfig[Config])
  }

  def load[F[_]: Sync](namespace: String)(implicit reader: Derivation[ConfigReader[Config]]): F[Config] = {
    suspendInF(pureconfig.loadConfig[Config](namespace))
  }

  private def suspendInF[F[_]: Sync](thunk: => Either[ConfigReaderFailures, Config]): F[Config] = {
    val F = Sync.apply[F]
    F.flatMap(F.delay(thunk)) {
      case Left(err) => F.raiseError(new IllegalArgumentException(s"Failed to read config file. ${err.toList.map(_.description).mkString(",")}"))
      case Right(c)  => F.pure(c)
    }
  }
}