package api.misc

object exceptions {
  case class UserNotFoundAtLoginException(reason: String) extends Throwable(reason)
  case class UserCreateFailedException(reason: String) extends Throwable(reason)
  case class UserUpdateFailedException(reason: String) extends Throwable(reason)
  case class UserDeleteFailedException(reason: String) extends Throwable(reason)
  case class UsernameTakenException(reason: String) extends Throwable(reason)
  case class PasswordNotHashableException(reason: String) extends Throwable(reason)

  case class UserUpdateSuccess(message: String) extends Throwable(message)
  case class UserCreateSuccess(message: String) extends Throwable(message)
  case class UserDeleteSuccess(message: String) extends Throwable(message)


  case class ProductCreateFailedException(reason: String) extends Throwable(reason)
  case class ProductUpdateFailedException(reason: String) extends Throwable(reason)

  case class ProductCreateSuccess(message: String) extends Throwable(message)
}

