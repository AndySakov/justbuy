package api.utils

object BCrypt {
  def hashpw(plaintext: String, salt: String): Either[String, String] =
    try {
      Right(org.mindrot.jbcrypt.BCrypt.hashpw(plaintext, salt))
    } catch {
      case e: Exception => Left(e.getMessage)
    }

  def hashpw(plaintext: String, logRounds: Int): Either[String, String] =
    try {
      Right(org.mindrot.jbcrypt.BCrypt.hashpw(plaintext, org.mindrot.jbcrypt.BCrypt.gensalt(logRounds)))
    } catch {
      case e: Exception => Left(e.getMessage)
    }

  def hashpw(plaintext: String): Either[String, String] =
    try {
      Right(org.mindrot.jbcrypt.BCrypt.hashpw(plaintext, org.mindrot.jbcrypt.BCrypt.gensalt))
    } catch {
      case e: Exception => Left(e.getMessage)
    }

  def checkpw(plaintext: String, hashed: String): Either[String, Boolean] =
    try {
      val valid = org.mindrot.jbcrypt.BCrypt.checkpw(plaintext, hashed)
      if (valid) {
        Right(true)
      } else {
        Left("Invalid password.")
      }
    } catch {
      case e: Exception => Left(e.getMessage)
    }
}
