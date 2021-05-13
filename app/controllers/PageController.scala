package controllers

import api.misc.exceptions.UserNotFoundAtLoginException
import auth.{AuthAction, AuthRequest}
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import dao.UserDAO
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to show pages
 */
@Singleton
class PageController @Inject()(authAction: AuthAction, users: UserDAO, val controllerComponents: ControllerComponents) extends BaseController {

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

  def red2L(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect("/login")
  }

  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.register())
  }

  def home(): Action[AnyContent] = authAction.async {
    implicit request: AuthRequest[AnyContent] =>
      users.getUser(request.userID) map {
        user => user match {
          case Left(_) => Forbidden("")
          case Right(userObj) => Ok(views.html.home(userObj))
        }
      }
  }

  def logout(): Action[AnyContent] = authAction {
    implicit request: AuthRequest[AnyContent] =>
      Redirect("/login").withNewSession
  }
}
