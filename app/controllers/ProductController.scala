package controllers

import auth.AuthAction
import dao.ProductDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

@Singleton
class ProductController @Inject()(products: ProductDAO, val controllerComponents: ControllerComponents, authAction: AuthAction) extends BaseController {

}
