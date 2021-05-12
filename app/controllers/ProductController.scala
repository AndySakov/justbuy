package controllers


import java.time.LocalDateTime

import api.misc.exceptions.{ProductCreateSuccess, ProductUpdateFailedException}
import api.utils.UUIDGenerator
import auth.{AuthAction, AuthRequest}
import dao.ProductDAO
import javax.inject.{Inject, Singleton}
import models.{Categories, Product}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
//import api.utils.Utils._

import scala.concurrent.Future

//case class Product(unique_id: String, name: String, desc: String, imgSrc: String, price: Double, brand: String, category: String, discount: Double,
// available: Boolean, stock: Int, toc: LocalDateTime)

@Singleton
class ProductController @Inject()(authAction: AuthAction, products: ProductDAO, val controllerComponents: ControllerComponents) extends BaseController {

  implicit val productWrites: Writes[Product] = (o: Product) => Json.obj(
    "unique_id" -> o.unique_id,
    "name" -> o.name,
    "desc" -> o.desc,
    "imgSrc" -> o.imgSrc,
    "price" -> o.price,
    "brand" -> o.brand,
    "category" -> o.category,
    "discount" -> o.discount,
    "available" -> o.available,
    "stock" -> o.stock,
    "toc" -> o.toc
  )

  def productFromJsRequest(data: JsValue, merch_id: String): Product = {
    val unique_id = UUIDGenerator.randomUUID
    val name = (data \ "name").as[String]
    val desc = (data \ "desc").as[String]
    val imgSrc = (data \ "imgSrc").as[String]
    val price = (data \ "price").as[Double]
    val brand = (data \ "brand").as[String]
    val category = (data \ "category").as[String]
    val discount = (data \ "discount").as[Double]
    val stock = (data \ "stock").as[Int]
    val toc = LocalDateTime.now()
    Product(unique_id, name, desc, imgSrc, price, brand, category, discount, available = true, stock, toc, merchant = merch_id)
  }


  def singleCreate(): Action[AnyContent] = authAction.async {
    implicit request: AuthRequest[AnyContent] =>
      request.body.asJson match {
        case Some(data) =>
          products.createProduct(productFromJsRequest(data, merch_id = request.userID)).map{
            _ => throw ProductCreateSuccess("Product created successfully!")
          }

        case None => Future.successful(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
     }
  }

  def multiCreate(): Action[AnyContent] = authAction.async{
    implicit request: AuthRequest[AnyContent] => {
      request.body.asJson match {
        case Some(data) =>
          val amt = (data \ "amount").as[Int]
          val _products = (1 to amt).map(x => productFromJsRequest((data \ "products" \ x).get, request.userID)).toList
          products.createProducts(_products).map {
            _ => throw ProductCreateSuccess("Product created successfully!")
          }

        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
    }
  }

  def getById(id: String): Action[AnyContent] = authAction.async{
    implicit request: AuthRequest[AnyContent] =>
      products.getProductById(unique_id = id).map(fo => Ok(Json.toJson(fo)))
  }

  def getByCat(cat: String): Action[AnyContent] = authAction.async{
    implicit request: AuthRequest[AnyContent] =>
      products.getProductsByCategory(category = Categories.withName(cat)).map(fo => Ok(Json.toJson(fo)))
  }

  def getAll: Action[AnyContent] = authAction.async {
    implicit request: AuthRequest[AnyContent] =>
      products.getProducts.map(fo => Ok(Json.toJson(fo)))
  }

  def updateProduct(id: String): Action[AnyContent] = authAction.async{
    implicit request: AuthRequest[AnyContent] =>
      request.body.asJson match {
        case Some(data) =>
          val part = (data \ "part").as[String]
          val _type = (data \ "type").as[String]
          val merchant = (data \ "merchant").as[String]
          products.getProductById(unique_id = id) map {
            case Some(head :: _) => if (head.merchant == request.userID) {
              products.updateProduct(head, part, {
                _type match {
                  case "String" => Left(part)
                  case "Double" => Right(part.toDouble)
                }
              }, merchant)
            } else {
              throw ProductUpdateFailedException("The product could not be updated")
            }
            case _ => throw ProductUpdateFailedException("The product could not be updated")
          }
          products.getProducts.map(fo => Ok(Json.toJson(fo)))
        case None => Future(Forbidden(Json.obj(("error", Json.toJson("Request contained no data!")))))
      }
  }

  def deleteProduct(id: String): Action[AnyContent] = authAction.async {
    implicit request: AuthRequest[AnyContent] =>
      products.deleteProduct(unique_id = id, request.userID)
      products.getProducts.map(fo => Ok(Json.toJson(fo)))
  }
}
