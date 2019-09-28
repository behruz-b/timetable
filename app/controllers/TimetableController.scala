package controllers

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import protocols.TimetableProtocol.{AddTimetable, GetText, GetTimetableByGroup, GetTimetableList, Timetable}
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

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map{ session =>
    Ok(timeTableTemplate())
    }.getOrElse {
      Unauthorized
    }
  }

  def dashboard: Action[AnyContent] = Action {
    Ok(timeTableDTemplate())
  }

  def addTimetable = Action.async(parse.json) { implicit request => {
    val studyShift = (request.body \ "studyShift").as[String]
    val weekDay = (request.body \ "weekDay").as[String]
    val couple = (request.body \ "couple").as[String]
    val typeOfLesson = (request.body \ "typeOfLesson").as[String]
    val groups = (request.body \ "groups").as[String]
    val divorce = (request.body \ "divorce").as[String]
    val subjectId = (request.body \ "subjectId").as[Int]
    val teachers = (request.body \ "teachers").as[String]
    val numberRoom = (request.body \ "numberRoom").as[Int]
    (timetableManager ? AddTimetable(Timetable(None, studyShift, weekDay, couple, typeOfLesson, groups, divorce, subjectId, teachers, numberRoom))).mapTo[Int].map { pr =>
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

  def hasGroup = Action.async(parse.json) {implicit request => {
    val group = (request.body \ "group").as[String]
    (timetableManager ? GetTimetableByGroup(GetText(currentDay(convertToStrDate(new Date)), group))).mapTo[String].map {
      timetable =>
        Ok(timetable)
    }
  }}

  def currentDay(shortDay: String) =  {
    shortDay match {
      case  "Mon" => "Monday"
      case  "Tues" => "Tuesday"
      case  "Wed" => "Wednesday"
      case  "Thurs" => "Thursday"
      case  "Fri" => "Friday"
      case  "Sat" => "Saturday"
      case  "Sun" => "Sunday"
    }
  }

  private def convertToStrDate(date: Date)
  = {
    new SimpleDateFormat("E").format(date)
  }



}
