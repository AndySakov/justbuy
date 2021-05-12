package dao


import api.misc.Message
import api.misc.exceptions._
import api.utils.BCrypt._
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class UserDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  type Cr8Result = (Boolean, Message.Value)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._

  private val Users = TableQuery[UsersTable]


  /**
   * Function to create a new user
   * @param newbie the user to create
   * @return a future with the result of the operation
   */
  def createUser(newbie: User): Future[Cr8Result] = db.run((Users += newbie.copy(pass = hashpw(newbie.pass).getOrElse(throw PasswordNotHashableException("Password could not be hashed!")))).asTry) map {
    case Failure(exception) => exception match {
      case _: SQLIntegrityConstraintViolationException => throw UsernameTakenException("Username is not available!")
      case _ => throw UserCreateFailedException("User account could not be created!")
    }
    case Success(_) => throw UserCreateSuccess("User created successfully!")
  }

  /**
   * Function to update a detail in a user entry
   * @param username the current username of the user
   * @param pass the current password of the user
   * @param part the detail to update
   * @param new_detail the new detail
   * @return a future with unit
   */
  def updateUser(username: String, pass: String, part: String, new_detail: String): Future[Unit] = {
    Await.result(getUser(username, pass), 10 seconds) match {
      case Left(_) => throw UserUpdateFailedException("Could not update user because user not found!")
      case Right(user) =>
        val op: DBIOAction[Unit, NoStream, Effect.Write] = part match {
          case "username" => Users.filter(x => x.unique_id === user.unique_id).map(_.username).update(new_detail).map(_ => ())
          case "password" => Users.filter(x => x.unique_id === user.unique_id).map(_.pass).update(hashpw(new_detail).getOrElse(throw PasswordNotHashableException("Password could not be hashed!"))).map(_ => ())
          case "fullname" => Users.filter(x => x.unique_id === user.unique_id).map(_.fullname).update(new_detail).map(_ => ())
          case "phone" => Users.filter(x => x.unique_id === user.unique_id).map(_.phone).update(new_detail).map(_ => ())
        }
        db.run(op)
    }
  }

  /**
   * Function to select a user entry in the database
   * @param username the username of the user to select
   * @param pass the password of the user to select
   * @return a future with a sequence containing the user if it exists
   */
  def getUser(username: String, pass: String): Future[Either[Boolean, User]] = {
    db.run(Users.filter(v => v.username === username).result) map {
      case result: Seq[UsersTable#TableElementType] => checkpw(pass, result.head.pass) match {
        case Right(_) => Right(result.head)
        case Left(_) => Left(false)
      }
      case _ => Left(false)
    }
  }

  /**
   * Function to delete a user entry from the database
   * @param username the username of the user to delete
   * @param pass the password of the user to delete
   * @return a future with unit
   */
  def deleteUser(username: String, pass: String): Future[Future[Either[Boolean, Boolean]]] = {
    getUser(username, pass) map {
      case Right(user) =>
        db.run{
          Users.filter(_.username === user.username).delete
        } map {
          case 1 => Right(true)
          case _ => Left(false)
        }
      case Left(_) => throw UserDeleteFailedException("")
    }
  }


  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def unique_id: Rep[String] = column[String]("UUID", O.Unique)
    def email: Rep[String] = column[String]("email")
    def phone: Rep[String] = column[String]("phone")
    def username: Rep[String] = column[String]("username", O.Unique, O.PrimaryKey)
    def pass: Rep[String] = column[String]("password")
    def fullname: Rep[String] = column[String]("fullname")
    def toc: Rep[LocalDateTime] = column[LocalDateTime]("TOC")

    def * = (unique_id, email, phone, username, pass, fullname, toc) <> (User.tupled, User.unapply)
  }
}