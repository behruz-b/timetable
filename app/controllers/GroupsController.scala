package controllers

import akka.actor.ActorRef
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import views.html._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

@Singleton
class GroupsController @Inject()(val controllerComponents: ControllerComponents,
//                                 @Named("group-manager") val groupManager: ActorRef,
                                 groupTemplate: group,
                                 )
                                (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(groupTemplate())
  }

  def getReportGroup = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    logger.info(name)
    Future(Ok(Json.toJson(name)))
  }

}
