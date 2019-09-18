package controllers

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import protocols.SubjectProtocol.{AddSubject, Subject}
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


  def teacherPost: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val fullname = (request.body \ "name").as[String]
    val tSubject = (request.body \ "name").as[String]
    val department = (request.body \ "numberClassRoom").as[String].toInt
    (teacherManager ? AddTeacher(Teacher(None, fullname, tSubject , department))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }
}
