package api.misc

trait Message {
  def message(message: Message.Value): String
}

object Message extends Enumeration with Message {
  type Message = Value
  val DUPLICATE_USER, UNKNOWN_ERROR, SUCCESS = Value

  override def message(message: Message.Value): String = message match {
    case DUPLICATE_USER => "User with this username already exists"
    case UNKNOWN_ERROR => "There was a problem creating your account, please try again later!"
    case SUCCESS => "User Account created successfully!"
  }
}
