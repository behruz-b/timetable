package controllers


import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, request, _}
import protocols.SubjectProtocol._
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class SubjectController @Inject()(val controllerComponents: ControllerComponents,
                                  @Named("subject-manager") val subjectManager: ActorRef,
                                  subjectTemplate: subject.subject,
                                  dashboardTemplate: subject.subject_dashboard,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map{ _ =>
      Ok(subjectTemplate(true))
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

  def subjectPost: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "name").as[String]
    (subjectManager ? AddSubject(Subject(None, name))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }

  def update: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "name").as[String]
    (subjectManager ? UpdateSubject(Subject(None, name))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"$pr"))
    }
  }
  }


  def getSubjects: Action[AnyContent] = Action.async { implicit request =>
    (subjectManager ? GetSubjectList).mapTo[Seq[Subject]].map {
      subject =>
        request.session.get(LoginSessionKey).map { _ =>
          Ok(Json.toJson(subject.sortBy(_.id)))
        }.getOrElse {
          Unauthorized
        }
    }
  }

  def getSortedSubject: Action[JsValue] = Action.async(parse.json)  { implicit request => {
    val key = (request.body \ "key").as[String]
    if (key == "id") {
      (subjectManager ? GetSubjectList).mapTo[Seq[Subject]].map {
        subject =>
          request.session.get(LoginSessionKey).map { _ =>
            Ok(Json.toJson(subject.sortBy(_.id)))
          }.getOrElse {
            Unauthorized
          }
      }
    }
    else if (key == "name") {
      (subjectManager ? GetSubjectList).mapTo[Seq[Subject]].map {
        subject =>
          request.session.get(LoginSessionKey).map { _ =>
            Ok(Json.toJson(subject.sortBy(_.name)))
          }.getOrElse {
            Unauthorized
          }
      }
    }
    else {
      (subjectManager ? GetSubjectList).mapTo[Seq[Subject]].map {
        subject =>
          request.session.get(LoginSessionKey).map{ _ =>
            Ok(Json.toJson(subject))
          }.getOrElse {
            Unauthorized
          }
      }
    }
  }}

  def getRooms = Action { implicit request => {
    request.session.get(LoginSessionKey).map{ _ =>
      Ok(Json.toJson(roomList))
    }.getOrElse {
      Unauthorized
    }
  }
  }
}