package controllers

import java.time.{LocalDate, LocalDateTime}

import api.misc.Message
import api.utils.UUIDGenerator.randomUUID
import auth.AuthAction
import dao.UserDAO
import javax.inject._
import models.User
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(users: UserDAO, val controllerComponents: ControllerComponents, authAction: AuthAction) extends BaseController {

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

  /**
   * A function to extract the body of a request sent as xxx-form-url-encoded
   * @param request the request to extract the body from
   * @return the extracted body
   */
  def body(implicit request: Request[AnyContent]): Option[Map[String, Seq[String]]] = {
    request.body.asFormUrlEncoded
  }

  /**
   * User creation handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `POST` request with
   * a path of `/create/user`.
   */
  def createUser(): Action[AnyContent] = authAction.async {
    implicit request: Request[AnyContent] => {
      body match {
        case Some(data) =>
          val username = data("username").head
          val pass = data("pass").head
          val fullname = data("fullname").head
          val dob = data("dob").head.split("-").map(_.toInt)
          users.createUser(User(randomUUID, username, pass, fullname, LocalDate.of(dob(2), dob(1), dob(0)), LocalDateTime.now())).map(x => Ok(Json.obj(
            ("success", Json.toJson(x._1)),
            ("message", Json.toJson(Message.message(x._2)))
          )))
        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
    }
  }

  /**
   * User update handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `PUT` request with
   * a path of `/update/user/:part` where part is the detail to update.
   */
  def updateUser(part: String): Action[AnyContent] = authAction.async {
    implicit request: Request[AnyContent] => {
      body match {
        case Some(data) =>
          val username = data("username").head
          val pass = data("pass").head
          val update = data("new_detail").head
          users.updateUser(users.getUser(username, pass), part, update).map(_ => Ok(Json.obj(("success", Json.toJson(true)))))
        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
    }
  }

  /**
   * User authentication handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `POST` request with
   * a path of `/auth/user`.
   */
  def authUser(): Action[AnyContent] = authAction.async{
    implicit request: Request[AnyContent] => {
      body match {
        case Some(data) =>
          val username = data("username").head
          val pass = data("pass").head
          users.getUser(username, pass).map(v => Ok(Json.obj(("success", Json.toJson(v.isEmpty)))))
        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
    }
  }

  /**
   * User deletion handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `DELETE` request with
   * a path of `/delete/user`.
   */
  def removeUser(): Action[AnyContent] = authAction.async {
    implicit request: Request[AnyContent] => {
      body match {
        case Some(data) =>
          val username = data("username").head
          val pass = data("pass").head
          users.deleteUser(username, pass).map(_ => Ok(Json.obj(("success", Json.toJson(true)))))
        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
    }
  }
}
 