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
import protocols.TeacherProtocol.DeleteTeacher
import protocols.TimetableProtocol._
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class TimetableController @Inject()(val controllerComponents: ControllerComponents,
                                    @Named("timetable-manager") val timetableManager: ActorRef,
                                    timeTableTemplate: timetable.timeTable,
                                    timeTableDTemplate: timetable.timetable_dashboard,
                                    realTemplate: timetable.realTimetable,
                                   )
                                   (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map { _ =>
      Ok(timeTableTemplate(true))
    }.getOrElse {
      Unauthorized
    }
  }

  def dashboard: Action[AnyContent] = Action {
    implicit request =>
      request.session.get(LoginSessionKey).map { _ =>
        Ok(timeTableDTemplate(true))
      }.getOrElse {
        Unauthorized
      }
  }

  def realDashboard: Action[AnyContent] = Action {
    Ok(realTemplate(false))
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
    (timetableManager ? AddTimetable(Timetable(None, studyShift, weekDay, couple, typeOfLesson, groups, divorce, subjectId, teachers, numberRoom))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }

  def delete: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val id = (request.body \ "id").as[String].toInt
    (timetableManager ? DeleteTimetable(id)).mapTo[Int].map { id =>
      Ok(Json.toJson(s"$id"))
    }
  }
  }

  def update = Action.async(parse.json) { implicit request => {
    val id = (request.body \ "id").as[String].toInt
    logger.warn(s"$id")
    val studyShift = (request.body \ "studyShift").as[String]
    val weekday = (request.body \ "weekday").as[String]
    val couple = (request.body \ "couple").as[String]
    val typeOfLesson = (request.body \ "type").as[String]
    val groups = (request.body \ "group").as[String]
    val divorce = if (typeOfLesson == "Laboratory") "half" else ""
    val subject = (request.body \ "subject").as[Int]
    val teacher = (request.body \ "teacher").as[String]
    val numberRoom = (request.body \ "numberRoom").as[String]
    (timetableManager ? UpdateTimetable(Timetable(Option(id), studyShift, weekday, couple, typeOfLesson, groups, divorce, subject, teacher, numberRoom))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
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

  //  implicit val groupTFormat = Json.format[GroupT]
  //  implicit val weekdayTFormat = Json.format[WeekdayT]
  implicit val groupTWrites: OWrites[GroupT] = Json.writes[GroupT]


  //  def grouppedTimetable = Action.async {
  //    (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
  //      timetable =>
  //        val grouped = timetable.groupBy(_.groups).map { g =>
  //          val weekdays = g._2.filter(_.groups == g._1).groupBy(_.weekDay).map {w =>
  //            WeekdayT(w._1, w._2)
  //          }.toSeq
  //          GroupT(g._1, weekdays)
  //        }
  //        Ok(Json.toJson(grouped))
  //    }
  //  }
  case class GT(groups: Set[String], timetables: Seq[Timetable])

  implicit val gtWrites = Json.writes[GT]

  def grouppedTimetable = Action.async {
    (timetableManager ? GetTimetableList).mapTo[Seq[Timetable]].map {
      timetable =>
        val grouped = timetable.map(_.groups).toSet
        Ok(Json.toJson(GT(grouped, timetable)))
    }
  }

  def hasGroup = Action.async(parse.json) { implicit request => {
    val data = (request.body \ "group").as[String].split("/").toList
    logger.warn(s"data: $data")
    logger.warn(s"data: $data")
    val whoisClient = data.head
    val when = data.reverse.tail.last
    val name = data.last
    if (whoisClient == "O'qituvchi") {
      if (when == "Bugun") {
        (timetableManager ? GetTimetableForTeacher(GetText(convertToStrDate(new Date), name))).mapTo[Seq[String]].map { timetable =>
          if (timetable.isEmpty) {
            logger.warn(s"Timetable is empty for group: $name")
            Ok(s"Bugun $name o'qituvchini darsi yo'q")
          } else {
            Ok(timetable.mkString("\n"))
          }
        }
      }
      else {
        (timetableManager ? TeacherName(name)).mapTo[Seq[String]].map { timetable =>
          if (timetable.isEmpty) {
            logger.warn(s"Timetable is empty for group: $name")
            Ok(s"$name ismli o'qituvchi yo'q")
          } else {
            Ok(timetable.mkString("\n"))
          }
        }
      }
    }
    else {
      if (when == "Bugun") {
        (timetableManager ? GetTimetableByGroup(GetText(convertToStrDate(new Date), name))).mapTo[Seq[String]].map { timetable =>
          if (timetable.isEmpty) {
            logger.warn(s"Timetable is empty for group: $name")
            Ok(s"Bugun $name guruhga dars yo'q")
          } else {
            Ok(timetable.mkString("\n"))
          }
        }
      }
      else if (when == "Ertaga") {
        (timetableManager ? GetTimetableByGroup(GetText(nextday(convertToStrDate(new Date)), name))).mapTo[Seq[String]].map { timetable =>
          if (timetable.isEmpty) {
            logger.warn(s"Timetable is empty for group: $name")
            Ok(s"Ertaga $name guruhga dars yo'q")
          } else {
            Ok(timetable.mkString("\n"))
          }
        }
      }
      else {
        (timetableManager ? Group(name)).mapTo[Seq[String]].map { timetable =>
          if (timetable.isEmpty) {
            logger.warn(s"Timetable is empty for group: $name")
            Ok(s"$name nomli guruh yo'q")
          } else {
            Ok(timetable.mkString("\n"))
          }
        }
      }
    }
  }
  }


  def test = Action(parse.json) { implicit request => {
    val test = (request.body \ "number").as[Int]
    logger.info(s"number: $test")
    Ok(Json.obj("response" -> test))
  }
  }

  def getTeacherTimetable = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "teacherName").as[String]
    logger.warn(s"name: $name")
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
    (timetableManager ? GetEmptyRoomByCouple(GetEmptyRoom(convertToStrDate(new Date), momentCouple(momentHourAndMinute(new Date))))).mapTo[Seq[String]].map {
      rooms =>
        Ok(Json.toJson(rooms))
    }
  }

  def momentCouple(time: String) = {
    val hour = time.substring(0, 2).toInt
    val minute = time.substring(3, 5).toInt
    if ((hour == 8 && (minute >= 30 && minute <= 59)) || (hour == 9 && (minute >= 0 && minute <= 50))) {
      "couple 2"
    }
    else if ((hour == 10 && minute >= 0 && minute <= 59) || (hour == 11 && minute >= 0 && minute <= 20)) {
      "couple 2"
    }
    else if ((hour == 11 && minute >= 30 && minute <= 59) || (hour == 12 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 13 && minute >= 30 && minute <= 59) || (hour == 14 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 15 && minute >= 0 && minute <= 59) || (hour == 16 && minute >= 0 && minute <= 20)) {
      "couple 2"
    }
    else if ((hour == 16 && minute >= 30 && minute <= 59) || (hour == 17 && minute >= 0 && minute <= 50)) {
      "couple 2"
    }
    else if ((hour == 17 && minute >= 51 && minute <= 59) || (hour >= 18 && minute >= 0 && minute <= 59) || (hour == 8 && minute >= 0 && minute <= 29) || (hour < 8 && minute >= 0 && minute <= 59)) {
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

  private def nextday(day: String) = {
    day match {
      case "Monday" => "Tuesday"
      case "Tuesday" => "Wednesday"
      case "Wednesday" => "Thursday"
      case "Thursday" => "Friday"
      case "Friday" => "Saturday"
      case "Saturday" => "Sunday"
      case "Sunday" => "Monday"
    }
  }

  private def momentHourAndMinute(date: Date)
  = {
    new SimpleDateFormat("HH:mm:ss").format(date)
  }


}
