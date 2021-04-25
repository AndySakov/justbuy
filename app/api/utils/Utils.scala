package api.utils

import io.circe.Json
import play.api.mvc.{AnyContent, Request}

object Utils {
  /**
   * A function to convert a string to a json object
   * @param text the string to convert
   * @return the json object representation of the input text
   */
  def jsonify(text: String): Json = {
    io.circe.parser.parse(text).getOrElse(Json.Null)
  }

  /**
   * A function to extract the body of a request sent as xxx-form-url-encoded
   * @param request the request to extract the body from
   * @return the extracted body
   */
  def body(implicit request: Request[AnyContent]): Option[Map[String, Seq[String]]] = {
    request.body.asFormUrlEncoded
  }
}
