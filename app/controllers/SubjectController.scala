package controllers


import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, _}
import protocols.SubjectProtocol.{AddSubject, GetSubjectLink, Subject}
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class SubjectController @Inject()(val controllerComponents: ControllerComponents,
                                  @Named("subject-manager") val subjectManager: ActorRef,
                                  subjectTemplate: subject,
                                  dashboardTemplate: subjectDashboard,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(subjectTemplate())
  }

  def dashboard: Action[AnyContent] = Action {
    Ok(dashboardTemplate())
  }

  def subjectPost: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "name").as[String]
    val numberClassRoom = (request.body \ "numberClassRoom").as[String].toInt
    (subjectManager ? AddSubject(Subject(None, name, numberClassRoom))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }
  def getReportSubject = Action.async {
    (subjectManager ? GetSubjectLink).mapTo[Seq[Subject]].map {
      subject =>
        Ok(Json.toJson(subject))
    }
  }

}