package protocols

import play.api.libs.json.{Json, OFormat}

object TimetableProtocol {

  case object GetTimetableList

  case class AddTimetable(timetable: Timetable)

  case class UpdateTimetable(timetable: Timetable)

  case class GetTimetableByGroup(getText: GetText)

  case class GetText(weekDay: String, group: String)

  implicit val getTextFormat: OFormat[GetText] = Json.format[GetText]

  case class TeacherName(teacher: String)

  implicit val TeacherNameFormat: OFormat[TeacherName] = Json.format[TeacherName]

  case class GetEmptyRoomByCouple(getCouple: GetEmptyRoom)

  case class GetEmptyRoom(weekDay: String, couple: String)

  implicit val getBusyRoomFormat: OFormat[GetEmptyRoom] = Json.format[GetEmptyRoom]

  case class Timetable(id: Option[Int] = None,
                       studyShift: String,
                       weekDay: String,
                       couple: String,
                       typeOfLesson: String,
                       groups: String,
                       divorce: String,
                       subjectId: Int,
                       teachers: String,
                       numberRoom: Int
                      )

  implicit val timetableFormat: OFormat[Timetable] = Json.format[Timetable]

  import play.api.libs.json._

  case class Group(group: String)

  implicit val groupWrites = Json.writes[Group]


}
