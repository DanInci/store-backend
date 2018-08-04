package store.db.config

import cats.effect.{Async, Sync}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
object DatabaseConfigAlgebra {

  def transactor[F[_]: Async](config: DatabaseConfig): F[Transactor[F]] = Async[F].delay {
    Transactor.fromDriverManager[F](config.driver, config.url, config.user, config.password)
  }

  def initializeSQLDb[F[_]: Sync](config: DatabaseConfig): F[Int] = Sync[F].delay {
    val fw = new Flyway()
    fw.setDataSource(config.url, config.user, config.password)
    if (config.clean) fw.clean()
    fw.migrate()
  }
}
