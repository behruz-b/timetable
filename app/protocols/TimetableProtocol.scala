package protocols

import play.api.libs.json.{Json, OFormat}

object TimetableProtocol {

  case object GetTimetableList

  case class AddTimetable(teacher: Timetable)

  case class GetTimetableByGroup(getText: GetText)

  case class GetText(weekDay: String, group: String)

  implicit val getTextFormat: OFormat[GetText] = Json.format[GetText]

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


}
