package api.utils

import io.circe.Json

object Utils {
  def jsonify(text: String): Json = {
    io.circe.parser.parse(text).getOrElse(Json.Null)
  }
}
