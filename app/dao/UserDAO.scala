package dao


import java.sql.SQLIntegrityConstraintViolationException
import java.time.{LocalDate, LocalDateTime}

import api.misc.Message
import javax.inject.Inject
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

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
  def createUser(newbie: User): Future[Cr8Result] = db.run((Users += newbie).asTry) map {
    case Failure(exception) => exception match {
      case _: SQLIntegrityConstraintViolationException => (false, Message.DUPLICATE_USER)
      case _ => (false, Message.UNKNOWN_ERROR)
    }
    case Success(_) => (true, Message.SUCCESS)
  }

  /**
   * Function to update a detail in a user entry
   * @param oldie the old version of the user entry
   * @param part the detail to update
   * @param new_detail the new detail
   * @return a future with unit
   */
  def updateUser(oldie: Future[Seq[User]], part: String, new_detail: String): Future[Unit] = {
    val user = Await.result(oldie, 10 seconds).head
    val op: DBIOAction[Unit, NoStream, Effect.Write] = part match {
      case "username" => Users.filter(x => x.unique_id === user.unique_id).map(_.username).update(new_detail).map(_ => ())
      case "pass" => Users.filter(x => x.unique_id === user.unique_id).map(_.pass).update(new_detail).map(_ => ())
      case "fullname" => Users.filter(x => x.unique_id === user.unique_id).map(_.fullname).update(new_detail).map(_ => ())
      case "phone" => Users.filter(x => x.unique_id === user.unique_id).map(_.phone).update(new_detail).map(_ => ())
    }
    db.run(op)
  }

  /**
   * Function to select a user entry in the database
   * @param username the username of the user to select
   * @param pass the password of the user to select
   * @return a future with a sequence containing the user if it exists
   */
  def getUser(username: String, pass: String): Future[Seq[User]] = {
    db.run[Seq[User]](Users.filter(v => v.username === username && v.pass === pass).result)
  }

  /**
   * Function to delete a user entry from the database
   * @param username the username of the user to delete
   * @param pass the password of the user to delete
   * @return a future with unit
   */
  def deleteUser(username: String, pass: String): Future[Unit] = {
    db.run(Users.filter(x => x.username === username && x.pass === pass).delete).map(_ => ())
  }


  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def unique_id: Rep[String] = column[String]("UUID", O.Unique)
    def email: Rep[String] = column[String]("email")
    def phone: Rep[String] = column[String]("phone")
    def username: Rep[String] = column[String]("username", O.Unique, O.PrimaryKey)
    def pass: Rep[String] = column[String]("password")
    def fullname: Rep[String] = column[String]("fullname")
    def dob: Rep[LocalDate] = column[LocalDate]("DOB")
    def toc: Rep[LocalDateTime] = column[LocalDateTime]("TOC")

    def * = (unique_id, username, email, phone, pass, fullname, dob, toc) <> (User.tupled, User.unapply)
  }
}