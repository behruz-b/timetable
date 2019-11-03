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
class GroupController @Inject()(val controllerComponents: ControllerComponents,
                                @Named("group-manager") val groupManager: ActorRef,
                                groupTemplate: group.group,
                                dashboardTemplate: group.group_dashboard,
                                )
                               (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map{ _ =>
      Ok(groupTemplate(true))
    }.getOrElse {
      Unauthorized
    }
  }

  def dashboard: Action[AnyContent] = Action { implicit request =>
  request.session.get(LoginSessionKey).map{ _ =>
    Ok(dashboardTemplate(true))
  }.getOrElse {
    Unauthorized
  }
  }

  def addGroup = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    val direction = (request.body \ "direction").as[String]
    (groupManager ? AddGroup(Group(None, name, direction, 0))).mapTo[Int].map {
      id =>
        Ok(Json.toJson(s"The Group number you entered is written by this  ID: $id"))
    }
  }

  def update = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[String].toInt
    val name = (request.body \ "name").as[String]
    val direction = (request.body \ "direction").as[String]
    val count = (request.body \ "count").as[Int]
    (groupManager ? UpdateGroup(Group(Option(id), name, direction, count))).mapTo[Int].map {
      id =>
        Ok(Json.toJson(s"ID: $id"))
    }
  }

  def delete = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[String].toInt
    (groupManager ? DeleteGroup(id)).mapTo[Int].map {
      id =>
        Ok(Json.toJson(s"ID: $id"))
    }
  }

  def getDirections = Action { implicit request => {
    request.session.get(LoginSessionKey).map{ session =>
      Ok(Json.toJson(directionsList))
    }.getOrElse {
      Unauthorized
    }
  }
  }

  def getGroupsList = Action.async { implicit request =>
    (groupManager ? GetGroupList).mapTo[Seq[Group]].map {
      group =>
        request.session.get(LoginSessionKey).map{ session =>
          Ok(Json.toJson(group))
        }.getOrElse {
          Unauthorized
        }
    }
  }




}
