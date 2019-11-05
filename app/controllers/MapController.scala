package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import protocols.GroupProtocol._
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class MapController @Inject()(val controllerComponents: ControllerComponents,
                              @Named("group-manager") val groupManager: ActorRef,
                              floor1Template: map.floor1,
                              floor2Template: map.floor2,
                              floor3Template: map.floor3,
                                )
                             (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {
  val LoginSessionKey = "login.key"

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def firstFloor: Action[AnyContent] = Action {implicit request: RequestHeader => {
      Ok(floor1Template(request.session.get(LoginSessionKey).isDefined))
  }}
  def secondFloor: Action[AnyContent] = Action {implicit request: RequestHeader => {
    Ok(floor2Template(request.session.get(LoginSessionKey).isDefined))
  }}
  def thirdFloor: Action[AnyContent] = Action {implicit request: RequestHeader => {
    Ok(floor3Template(request.session.get(LoginSessionKey).isDefined))
  }}

}
