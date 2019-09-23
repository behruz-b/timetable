package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import protocols.TimetableProtocol.{AddTimetable, GetTimetableList, Timetable}
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TimetableController @Inject()(val controllerComponents: ControllerComponents,
                                    @Named("timetable-manager") val timetableManager: ActorRef,
                                    timeTableTemplate: timeTable,
                                    timeTableDTemplate: timetable_dashboard,
                                   )
                                   (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(timeTableTemplate())
  }

  def dashboard: Action[AnyContent] = Action {
    Ok(timeTableDTemplate())
  }


  def addTimetable = Action.async(parse.json) { implicit request => {
    val studyShift = (request.body \ "studyShift").as[String]
    val weekDay = (request.body \ "weekDay").as[String]
    val couple = (request.body \ "couple").as[String]
    val typeOfLesson = (request.body \ "typeOfLesson").as[String]
    val groups = (request.body \ "groups").as[List[String]]
    val subjectId = (request.body \ "subjectId").as[Int]
    val teachers = (request.body \ "teachers").as[List[String]]
    val numberRoom = (request.body \ "numberRoom").as[List[Int]]
    (timetableManager ? AddTimetable(Timetable(None, studyShift, weekDay, couple, typeOfLesson, groups, subjectId, teachers, numberRoom))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }

  def getReportTimetable = Action.async {
    (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
      timetable =>
        Ok(Json.toJson(timetable))
    }
  }


}
