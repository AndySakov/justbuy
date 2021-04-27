package api.misc

object exceptions {
  case class UserNotFoundAtLoginException(reason: String) extends Throwable(reason)
  case class UserCreateFailedException(reason: String) extends Throwable(reason)
  case class UserUpdateFailedException(reason: String) extends Throwable(reason)
  case class UserDeleteFailedException(reason: String) extends Throwable(reason)
  case class PasswordNotHashableException(reason: String) extends Throwable(reason)
  case class UserUpdateSuccess(message: String) extends Throwable(message)
  case class UserCreateSuccess(message: String) extends Throwable(message)
  case class UserDeleteSuccess(message: String) extends Throwable(message)
  case class UsernameTakenException(reason: String) extends Throwable(reason)
}

