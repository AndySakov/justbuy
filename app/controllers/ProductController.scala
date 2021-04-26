package controllers

import dao.ProductDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

@Singleton
class ProductController @Inject()(products: ProductDAO, val controllerComponents: ControllerComponents) extends BaseController {

}
