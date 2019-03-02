package store.service.user

import org.http4s.HttpService
import store.algebra.auth.ModuleAuthAsync
import store.service.user.rest.UserAuthRestService

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
trait ModuleUserServiceAsync[F[_]] { this: ModuleAuthAsync[F]  =>

  def userModuleService: HttpService[F] = _userRestService.service

  private lazy val _userRestService: UserAuthRestService[F] =
    new UserAuthRestService[F](
      authAlgebra = authAlgebra
    )

}
