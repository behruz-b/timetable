package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import protocols.TeacherProtocol._
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TeacherController @Inject()(val controllerComponents: ControllerComponents,
                                  @Named("teacher-manager") val teacherManager: ActorRef,
                                  teachersTemplate: teacher.teachers,
                                  teachersDTemplate: teacher.teacher_dashboard,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map{ session =>
      Ok(teachersTemplate())
    }.getOrElse {
      Unauthorized
    }
  }

  def dashboard: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map{ session =>
      Ok(teachersDTemplate())
    }.getOrElse {
      Unauthorized
    }
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

  def getReportTeacher: Action[AnyContent] = Action.async { implicit request =>
    (teacherManager ? GetTeacherList).mapTo[Seq[Teacher]].map {
      teachers =>
        request.session.get(LoginSessionKey).map{ session =>
          Ok(Json.toJson(teachers))
        }.getOrElse {
          Unauthorized
        }
    }
  }

  def getReportTeacherByTS: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val tSubject = (request.body \ "tSubject").as[String]
    (teacherManager ? GetTeacherListByTS(tSubject)).mapTo[Seq[Teacher]].map {
      teachers =>
        request.session.get(LoginSessionKey).map { session =>
          Ok(Json.toJson(teachers))
        }.getOrElse {
          Unauthorized
        }
    }
  }
  }

  def getDepartment = Action { implicit request => {
    request.session.get(LoginSessionKey).map{ session =>
      Ok(Json.toJson(directionsList))
    }.getOrElse {
      Unauthorized
    }
  }
  }

}