import api.misc.exceptions._
import javax.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import api.utils.Utils.flash

import scala.concurrent._

@Singleton
class ErrorHandler @Inject() (
                               env: Environment,
                               config: Configuration,
                               sourceMapper: OptionalSourceMapper,
                               router: Provider[Router]
                             ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {
  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  override def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(
      Forbidden("You're not allowed to access this resource.")
    )
  }

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(
      NotFound(views.html.err404())
    )
  }


  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case UserNotFoundAtLoginException(reason) => Future.successful(Redirect("/login").flashing(flash(reason, "danger"): _*))
      case UsernameTakenException(reason) => Future.successful(Redirect("/create").flashing(flash(reason, "danger"): _*))
      case UserCreateFailedException(reason) => Future.successful(Redirect("/create").flashing(flash(reason, "danger"): _*))

      case UserCreateSuccess(reason) => Future.successful(Redirect("/login").flashing(flash(reason, "success"): _*))
      case UserUpdateSuccess(reason) => Future.successful(Redirect("/login").flashing(flash(reason, "success"): _*).withNewSession)
      case UserDeleteSuccess(reason) => Future.successful(Redirect("/login").flashing(flash(reason, "success"): _*).withNewSession)

      case _ => super.onServerError(request, exception)
    }
  }
}