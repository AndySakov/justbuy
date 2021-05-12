package auth

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

// A custom request type to hold our JWT claims, we can pass these on to the
// handling action
case class AuthRequest[A](request: Request[A], userID: String) extends WrappedRequest[A](request)

// Our custom action implementation
class AuthAction @Inject()(bodyParser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // Called when a request is invoked. We should validate the request here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] =
    authSession(request) match {
      case Some(authRequest) => block(authRequest)
      case None => Future.successful(Results.Unauthorized(views.html.err401()))
    }

  // Helper for extracting the token value
  private def authSession[A](request: Request[A]): Option[AuthRequest[A]] =
    request.session.get("username") match {
      case Some(value) => Some(AuthRequest(request, value))
      case None => None
    }
}