package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import protocols.TeacherProtocol.{AddTeacher, Teacher}
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TeacherController @Inject()(val controllerComponents: ControllerComponents,
                                  @Named("teacher-manager") val teacherManager: ActorRef,
                                  teachersTemplate: teachers,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(teachersTemplate())
  }

  def addTeacher: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val fullName = (request.body \ "fullName").as[String]
    val tSubject = (request.body \ "tSubject").as[String]
    val department = (request.body \ "department").as[String]
    (teacherManager ? AddTeacher(Teacher(None, fullName, tSubject, department))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }
}