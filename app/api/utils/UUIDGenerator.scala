package api.utils

import scala.util.Random

object UUIDGenerator {
  private val all: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
  private val space: Int = all.length

  def random(i: Int): String = {
    val token = for(_ <- 1 to i) yield {
      all(Random.nextInt(space))
    }
    token.mkString("")
  }

  def randomUUID: String = {
    random(20)
  }
}
