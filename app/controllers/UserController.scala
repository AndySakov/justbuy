package controllers

import java.time.{LocalDate, LocalDateTime}

import api.misc.Message
import api.utils.UUIDGenerator.randomUUID
import dao.UserDAO
import javax.inject._
import models.User
import play.api.libs.json.Json
import play.api.mvc._
import api.utils.Utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests that alter the users table
 */
@Singleton
class UserController @Inject()(users: UserDAO, val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * User creation handler
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `POST` request with
   * a path of `/create/user`.
   */
  def createUser(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] => {
      body match {
        case Some(data) =>
          val username = data("username").head
          val email = data("email").head
          val phone = data("email").head
          val pass = data("pass").head
          val fullname = data("fullname").head
          users.createUser(User(unique_id = randomUUID, username = username, email = email, phone = phone, pass = pass, fullname = fullname, toc = LocalDateTime.now())).map(x => Ok(Json.obj(
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
  def updateUser(part: String): Action[AnyContent] = Action.async {
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
  def authUser(): Action[AnyContent] = Action.async{
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
  def removeUser(): Action[AnyContent] = Action.async {
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
 