package controllers

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json, OWrites}
import play.api.mvc._
import protocols.TimetableProtocol.{TimetableOwner, _}
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TimetableController @Inject()(val controllerComponents: ControllerComponents,
                                    @Named("timetable-manager") val timetableManager: ActorRef,
                                    timetableTemplate: timetable.timeTable,
                                    timetableDTemplate: timetable.timetable_dashboard,
                                    realTemplate: timetable.realTimetable,
                                    todayST: timetable.timetableforBot.forStudentToday,
                                    todayTT: timetable.timetableforBot.forTeacherToday,
                                    weekTT: timetable.timetableforBot.forTeacherWeek,
                                    tomorrowST: timetable.timetableforBot.forStudentTomorrow,
                                    weekST: timetable.timetableforBot.forStudentWeek,
                                   )
                                   (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map { _ =>
      Ok(timetableTemplate(true))
    }.getOrElse {
      Unauthorized
    }
  }

  def todayStudent(requiredData: String) = Action {
    Ok(todayST(logged = false, requiredData))
  }

  def todayTeacher(requiredData: String) = Action {
    Ok(todayTT(logged = false, requiredData))
  }

  def weekTeacher(requiredData: String) = Action {
    Ok(weekTT(logged = false, requiredData))
  }

  def tomorrowStudent(requiredData: String) = Action {
    Ok(tomorrowST(logged = false, requiredData))
  }

  def weekStudent(requiredData: String) = Action {
    Ok(weekST(logged = false, requiredData))
  }

  def dashboard: Action[AnyContent] = Action {
    implicit request =>
      request.session.get(LoginSessionKey).map { _ =>
        Ok(timetableDTemplate(true))
      }.getOrElse {
        Unauthorized
      }
  }

  def realDashboard: Action[AnyContent] = Action { implicit request: RequestHeader => {
    Ok(realTemplate(!request.session.get(LoginSessionKey).isEmpty))
  }
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
    val numberRoom = (request.body \ "numberRoom").as[String]
    val flow = (request.body \ "flow").as[Boolean]
    val alternation = (request.body \ "alternation").asOpt[String]
    (timetableManager ? AddTimetable(Timetable(None, studyShift, weekDay, couple, typeOfLesson, groups, divorce, subjectId, teachers, numberRoom, None, flow, alternation))).mapTo[Either[String, String]].map {
      case Right(str) =>
        Ok(Json.toJson(str))
      case Left(err) =>
        Ok(err)
    }
  }
  }

  def delete: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val id = (request.body \ "id").as[String].toInt
    (timetableManager ? DeleteTimetable(id)).mapTo[Int].map { id =>
      Ok(Json.toJson(s"$id raqamli dars jadvali muvoffaqiyatli o'chirildi!"))
    }
  }
  }

  def update = Action.async(parse.json) { implicit request => {
    logger.warn(s"request: ${request.body}")
    val id = (request.body \ "id").as[String].toInt
    val studyShift = (request.body \ "studyShift").as[String]
    val weekday = (request.body \ "weekday").as[String]
    val couple = (request.body \ "couple").as[String]
    val typeOfLesson = (request.body \ "type").as[String]
    val groups = (request.body \ "group").as[String]
    val divorce = if (typeOfLesson == "Laboratory") "half" else ""
    val subject = (request.body \ "subject").as[Int]
    val teacher = (request.body \ "teacher").as[String]
    val numberRoom = (request.body \ "numberRoom").as[String]
    val flow = (request.body \ "flow").as[Boolean]
    val alternation = (request.body \ "alternation").asOpt[String]
    if (typeOfLesson == "Laboratory") {
      (timetableManager ? UpdateTimetable(Timetable(Option(id), studyShift, weekday, couple, typeOfLesson, groups, divorce, subject, teacher, numberRoom, None, flow, alternation, Some(Json.toJson(numberRoom, teacher))))).mapTo[Option[Int]].map { id =>
        val pr = id.toString.replace("Some(", "").replace(")", "")
        Ok(Json.toJson(s"$pr raqamli dars jadvali muvoffaqiyatli yangilandi!"))
      }
    }else {
      (timetableManager ? UpdateTimetable(Timetable(Option(id), studyShift, weekday, couple, typeOfLesson, groups, divorce, subject, teacher, numberRoom, None, flow, alternation))).mapTo[Option[Int]].map { id =>
        val pr = id.toString.replace("Some(", "").replace(")", "")
        Ok(Json.toJson(s"$pr raqamli dars jadvali muvoffaqiyatli yangilandi!"))
      }
    }
  }
  }

  def getReportTimetable = Action.async {
    (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
      timetable =>
        val grouped = timetable.groupBy(_.groups)
        Ok(Json.toJson(grouped))
    }
  }

  case class GroupT(group: String, weekdays: Seq[WeekdayT])

  implicit val weekdayTWrites: OWrites[WeekdayT] = Json.writes[WeekdayT]

  case class WeekdayT(weekday: String, timetable: Seq[Timetable])

  implicit val groupTWrites: OWrites[GroupT] = Json.writes[GroupT]

  case class GT(groups: Seq[String], timetables: Seq[Timetable])

  implicit val gtWrites = Json.writes[GT]

  case class TT(teacher: Seq[String], timetables: Seq[Timetable])

  implicit val ttWrites = Json.writes[TT]

  def grouppedTimetable = Action.async {
    (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map { timetable =>
      val grouped = timetable.map(_.groups).sortBy(num => (num.split("-").map(_.toInt).last, num.split("-").map(_.toInt).head)).distinct
      Ok(Json.toJson(GT(grouped, timetable.sortBy(_.couple))))
    } 
  }

  def hasGroup = Action.async(parse.json) { implicit request => {
    val data = (request.body \ "requiredData").as[String].toString.split("_").toList
    val whoIsClient = data.head
    val when = data.reverse.tail.head
    val name = data.last
    logger.warn(s"${convertToStrDate(new Date)}: $data")

    def getTimetableWithDate(whom: TimetableWithDateOwner, who: String) = {
      (timetableManager ? whom).mapTo[Seq[Timetable]].map { timetable =>
        if (timetable.isEmpty) {
          Ok(s"No")
        } else {
          if (who == "student") {
            val grouped = timetable.map(_.groups).sorted.distinct
            Ok(Json.toJson(GT(grouped, timetable.sortBy(_.couple))))
          }
          else {
            val grouped = timetable.map(_.teachers).sorted.distinct
            Ok(Json.toJson(TT(grouped, timetable.sortBy(_.couple))))
          }
        }
      }
    }

    def getTimetable(whom: TimetableOwner, who: String) = {
      (timetableManager ? whom).mapTo[Seq[Timetable]].map { timetable =>
        if (timetable.isEmpty) {
          Ok(s"No")
        } else {
          if (who == "student") {
            val grouped = timetable.map(_.groups).sorted.distinct
            Ok(Json.toJson(GT(grouped, timetable.sortBy(_.couple))))
          }
          else {
            val grouped = timetable.map(_.teachers).sorted.distinct
            Ok(Json.toJson(TT(grouped, timetable.sortBy(_.couple))))
          }
        }
      }
    }


    whoIsClient match {
      case "teacher" =>
        if (when == "today") {
          getTimetableWithDate(GetTimetableForTeacher(GetTeacher(convertToStrDate(new Date), name)), s"teacher")
        }
        else {
          getTimetable(GetTTeacher(name),s"teacher")
        }
      case _ =>
        if (when == "today") {
          getTimetableWithDate(GetTimetableByGroup(GetText(convertToStrDate(new Date), name)), s"student")
        }
        else if (when == "tomorrow") {
          getTimetableWithDate(GetTimetableByGroup(GetText(nextday(convertToStrDate(new Date)), name)), s"student")
        }
        else {
          getTimetable(TimetableForGroup(name), s"student")
        }
    }
  }
  }

  def test = Action(parse.json) { implicit request => {
    val test = (request.body \ "number").as[Int]
    Ok(Json.obj("response" -> test))
  }
  }

  def getTeacherTimetable = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "teacherName").as[String]
    if (name != "") {
      (timetableManager ? TeacherName(name)).mapTo[Seq[Timetable]].map {
        timetable =>
          val grouped = timetable.groupBy(_.groups)
          Ok(Json.toJson(grouped))
      }
    }
    else {
      (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
        timetable =>
          val grouped = timetable.groupBy(_.groups)
          Ok(Json.toJson(grouped))
      }
    }

  }
  }

  def getGroupTimetable = Action.async(parse.json) { implicit request => {
    val groupName = (request.body \ "groupNumber").as[String]
    if (groupName != "") {
      (timetableManager ? GetTimetableByGr(groupName)).mapTo[Seq[Timetable]].map {
        timetable =>
          val grouped = timetable.groupBy(_.groups)
          Ok(Json.toJson(grouped))
      }
    }
    else {
      (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
        timetable =>
          val grouped = timetable.groupBy(_.groups)
          Ok(Json.toJson(grouped))
      }
    }
  }
  }

  def emptyRoom = Action.async {
    (timetableManager ? GetEmptyRoomByCouple(GetEmptyRoom(convertToStrDate(new Date),
      momentCouple(momentHourAndMinute(new Date)),
      momentStudyShift(momentHourAndMinute(new Date))))).mapTo[Seq[String]].map {
      rooms =>
        Ok(Json.toJson(rooms))
    }
  }

  def momentCouple(time: String) = {
    val hour = time.substring(0, 2).toInt
    val minute = time.substring(3, 5).toInt
    if ((hour == 8 && (minute >= 30 && minute <= 59)) || (hour == 9 && (minute >= 0 && minute <= 50))) {
      "couple 1"
    }
    else if ((hour == 10 && minute >= 0 && minute <= 59) || (hour == 11 && minute >= 0 && minute <= 20)) {
      "couple 2"
    }
    else if ((hour == 11 && minute >= 30 && minute <= 59) || (hour == 12 && minute >= 0 && minute <= 50)) {
      "couple 3"
    }
    else if ((hour == 13 && minute >= 30 && minute <= 59) || (hour == 14 && minute >= 0 && minute <= 50)) {
      "couple 1"
    }
    else if ((hour == 15 && minute >= 0 && minute <= 59) || (hour == 16 && minute >= 0 && minute <= 20)) {
      "couple 2"
    }
    else if ((hour == 16 && minute >= 30 && minute <= 59) || (hour == 17 && minute >= 0 && minute <= 50)) {
      "couple 3"
    }
    else if ((hour == 17 && minute >= 51 && minute <= 59) || (hour >= 18 && minute >= 0 && minute <= 59) || (hour == 8 && minute >= 0 && minute <= 29) || (hour < 8 && minute >= 0 && minute <= 59)) {
      "Dars tugadi!"
    }
    else {
      "Tanaffus"
    }
  }

  def momentStudyShift(time: String) = {
    val hour = time.substring(0, 2).toInt
    val minute = time.substring(3, 5).toInt
    if ((hour >= 9 && hour <= 11) || (hour == 8 && (minute >= 30 && minute <= 59)) || (hour == 12 && (minute >= 0 && minute <= 50))) {
      "Morning"
    }
    else if ((hour >= 14 && hour <= 16) || (hour == 13 && (minute >= 30 && minute <= 59)) || (hour == 17 && (minute >= 0 && minute <= 50))) {
      "Afternoon"
    }
    else {
      "Dars tugadi!"
    }
  }

  def today = Action {
    Ok(Json.toJson(translateWeekday(convertToStrDate(new Date))))
  }


  def tomorrow = Action {
    Ok(Json.toJson(translateWeekday(nextday(convertToStrDate(new Date)))))
  }

  private def convertToStrDate(date: Date)
  = {
    new SimpleDateFormat("EEEE").format(date)
  }

  private def nextday(day: String) = {
    day match {
      case "Monday" => "Tuesday"
      case "Tuesday" => "Wednesday"
      case "Wednesday" => "Thursday"
      case "Thursday" => "Friday"
      case "Friday" => "Saturday"
      case "Saturday" => "Monday"
      case "Sunday" => "Monday"
    }
  }

  private def translateWeekday(weekday: String) = {
    weekday match {
      case "Monday" => "Dushanba"
      case "Tuesday" => "Seshanba"
      case "Wednesday" => "Chorshanba"
      case "Thursday" => "Payshanba"
      case "Friday" => "Juma"
      case "Saturday" => "Shanba"
    }
  }

  private def momentHourAndMinute(date: Date)
  = {
    new SimpleDateFormat("HH:mm:ss").format(date)
  }
}
