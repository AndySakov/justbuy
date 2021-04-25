package controllers

import auth.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

/**
 * This controller creates an `Action` to handle HTTP requests to show pages
 */
@Singleton
class PageController @Inject()(val controllerComponents: ControllerComponents, authAction: AuthAction) extends BaseController {

  /**
   * Error 404 custom return handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/` or any other undefined path.
   */
  def fof(O: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    O.toSeq
    NotFound(views.html.err404())
  }

}
