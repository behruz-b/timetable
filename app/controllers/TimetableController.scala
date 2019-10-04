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
import protocols.TimetableProtocol._
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TimetableController @Inject()(val controllerComponents: ControllerComponents,
                                    @Named("timetable-manager") val timetableManager: ActorRef,
                                    timeTableTemplate: timetable.timeTable,
                                    timeTableDTemplate: timetable.timetable_dashboard,
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
    (timetableManager ? GetTimetableByGroup(GetText(convertToStrDate(new Date), group))).mapTo[Seq[String]].map {
      timetable =>
        Ok(timetable.mkString("\n"))
    }
  }}

  def getTeacherTimetable = Action.async(parse.json) {implicit request => {
    val name = (request.body \ "teacherName").as[String]
    (timetableManager ? TeacherName(name)).mapTo[Seq[Timetable]].map {
      timetable =>
        Ok(Json.toJson(timetable))
    }
  }}

  def emptyRoom = Action.async {
    (timetableManager ? GetEmptyRoomByCouple(GetEmptyRoom("Tuesday","couple 1"))).mapTo[Seq[Int]].map {
      rooms =>
        Ok(Json.toJson(rooms))
    }
  }

  def momentCouple(time: String) = {
    val hour = time.substring(0, 2).toInt
    val minute = time.substring(3, 5).toInt
    if ((hour == 8  && (minute >= 30 && minute <=59)) || (hour == 9 && (minute >= 0 && minute <= 50))) {
      "couple 2"
    }
    else if ((hour == 10  && minute >= 0 && minute <= 59) || (hour == 11 && minute >= 0 && minute <= 20)){
      "couple 2"
    }
    else if ((hour == 11  && minute >= 30 && minute <= 59) || (hour == 12 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 13  && minute >= 30 && minute <= 59) || (hour == 14 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 15  && minute >= 0 && minute <= 59) || (hour == 16 && minute >= 0 && minute <= 20)) {
      "couple 2"
    }
    else if ((hour == 16  && minute >= 30 && minute <= 59) || (hour == 17 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 17  && minute >= 51 && minute <=59) || (hour >= 18 && minute >= 0 && minute <=59) || (hour == 8 && minute >= 0 && minute <=29) || (hour < 8 && minute >= 0 && minute <=59) ) {
      "Dars tugadi!"
    }
    else {
      "Tanaffus"
    }
  }

  private def convertToStrDate(date: Date)
  = {
    new SimpleDateFormat("EEEE").format(date)
  }

  private def momentHourAndMinute(date: Date)
  = {
    new SimpleDateFormat("HH:mm:ss").format(date)
  }



}
