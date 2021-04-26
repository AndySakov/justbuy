package dao


import java.time.LocalDateTime

import api.misc.Message
import models.Categories.Category
import javax.inject.Inject
import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class ProductDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  type Cr8Result = (Boolean, Message.Value)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._

  private val Products = TableQuery[ProductsTable]

  /**
   * Function to create a new product
   * @param newbie the product to create
   * @return a future with the result of the operation
   */
  def createProduct(newbie: Product): Future[Cr8Result] = db.run((Products += newbie).asTry) map {
    case Failure(exception) => exception match {
      case _ => (false, Message.UNKNOWN_ERROR)
    }
    case Success(_) => (true, Message.SUCCESS)
  }

  /**
   * Function to create new products
   * @param newbies the list of products to create
   * @return a future with the result of the operation
   */
  def createProducts(newbies: List[Product]): Future[Cr8Result] = db.run((Products ++= newbies).asTry) map {
    case Failure(exception) => exception match {
      case _ => (false, Message.UNKNOWN_ERROR)
    }
    case Success(_) => (true, Message.SUCCESS)
  }

  /**
   * Function to update a detail in a product entry
   * @param oldie the old version of the product entry
   * @param part the detail to update
   * @param new_value the new value to update the entry with
   * @return a future with unit
   */
  def updateProduct(oldie: Future[Seq[Product]], part: String, new_value: Either[String, Double]): Future[Unit] = {
    val product = Await.result(oldie, 10 seconds).head
    val op: DBIOAction[Unit, NoStream, Effect.Write] = new_value match {
      case Left(value) =>
        part match {
          case "name" => Products.filter(x => x.unique_id === product.unique_id).map(_.name).update(value).map(_ => ())
          case "brand" => Products.filter(x => x.unique_id === product.unique_id).map(_.brand).update(value).map(_ => ())
          case "category" => Products.filter(x => x.unique_id === product.unique_id).map(_.category).update(value).map(_ => ())
          case "description" => Products.filter(x => x.unique_id === product.unique_id).map(_.desc).update(value).map(_ => ())
          case "imgSrc" => Products.filter(x => x.unique_id === product.unique_id).map(_.imgSrc).update(value).map(_ => ())
          case "available" => Products.filter(x => x.unique_id === product.unique_id).map(_.available).update(value.toBoolean).map(_ => ())
        }
      case Right(value) => part match {
        case "price" => Products.filter(x => x.unique_id === product.unique_id).map(_.price).update(value).map(_ => ())
        case "discount" => Products.filter(x => x.unique_id === product.unique_id).map(_.discount).update(value).map(_ => ())
        case "stock" => Products.filter(x => x.unique_id === product.unique_id).map(_.stock).update(value.toInt).map(_ => ())
      }
    }

    db.run(op)
  }

  /**
   * Function to update a detail in several product entries
   * @param oldies the old version of the product entry
   * @param part the detail to update
   * @param new_value the new value to update the entry with
   * @return a future with unit
   */
  def updateProducts(oldies: Future[Seq[Product]], part: String, new_value: Either[String, Double]): Future[Unit] = {
    val ops = Await.result(oldies, 10 seconds).map{
      product =>
        new_value match {
          case Left(value) =>
            part match {
              case "name" => Products.filter(x => x.unique_id === product.unique_id).map(_.name).update(value).map(_ => ())
              case "brand" => Products.filter(x => x.unique_id === product.unique_id).map(_.brand).update(value).map(_ => ())
              case "category" => Products.filter(x => x.unique_id === product.unique_id).map(_.category).update(value).map(_ => ())
              case "description" => Products.filter(x => x.unique_id === product.unique_id).map(_.desc).update(value).map(_ => ())
              case "imgSrc" => Products.filter(x => x.unique_id === product.unique_id).map(_.imgSrc).update(value).map(_ => ())
              case "available" => Products.filter(x => x.unique_id === product.unique_id).map(_.available).update(value.toBoolean).map(_ => ())
            }
          case Right(value) => part match {
            case "price" => Products.filter(x => x.unique_id === product.unique_id).map(_.price).update(value).map(_ => ())
            case "discount" => Products.filter(x => x.unique_id === product.unique_id).map(_.discount).update(value).map(_ => ())
            case "stock" => Products.filter(x => x.unique_id === product.unique_id).map(_.stock).update(value.toInt).map(_ => ())
          }
        }
    }

    db.run(DBIO.seq(ops: _*).transactionally)
  }

  /**
   * Function to select a product entry in the database by its ID
   * @param unique_id the unique id of the product to select
   * @return a future with a sequence containing the product if it exists
   */
  def getProductById(unique_id: String): Future[Seq[Product]] = {
    db.run[Seq[Product]](Products.filter(v => v.unique_id === unique_id).result)
  }

  /**
   * Function to select a product entry in the database by its ID
   * @return a future with a sequence containing all the available products
   */
  def getProducts: Future[Seq[Product]] = {
    db.run[Seq[Product]](Products.result)
  }

  /**
   * Function to select a product entry in the database by its ID
   * @param category the category of products to select
   * @return a future with a sequence containing the product if it exists
   */
  def getProductsByCategory(category: Category): Future[Seq[Product]] = {
    db.run[Seq[Product]](Products.filter(v => v.category === category.toString ).result)
  }

  /**
   * Function to delete a product entry from the database
   * @param unique_id the unique id of the product to delete
   * @return a future with unit
   */
  def deleteProduct(unique_id: String): Future[Unit] = {
    db.run(Products.filter(x => x.unique_id === unique_id).delete).map(_ => ())
  }


  class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
    def unique_id: Rep[String] = column[String]("PID", O.Unique, O.PrimaryKey)
    def name: Rep[String] = column[String]("PNAME")
    def desc: Rep[String] = column[String]("PDESC")
    def imgSrc: Rep[String] = column[String]("PISRC")
    def price: Rep[Double] = column[Double]("PRICE")
    def brand: Rep[String] = column[String]("PBRAND")
    def category: Rep[String] = column[String]("PCAT")
    def discount: Rep[Double] = column[Double]("PDISC")
    def available: Rep[Boolean] = column[Boolean]("PAVAIL")
    def stock: Rep[Int] = column[Int]("PSTOCK")
    def toc: Rep[LocalDateTime] = column[LocalDateTime]("PTOC")

    def * = (unique_id, name, desc, imgSrc, price, brand, category, discount, available, stock, toc) <> (Product.tupled, Product.unapply)
  }
}