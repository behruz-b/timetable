package protocols

import play.api.libs.json.{Json, OFormat}

object TimetableProtocol {

  case object GetTimetableList

  case class AddTimetable(teacher: Timetable)

  case class Timetable(id: Option[Int] = None,
                       studyShift: String,
                       weekDay: String,
                       couple: String,
                       typeOfLesson: String,
                       groups: List[String],
                       subjectId: Int,
                       teachers: List[String],
                       numberRoom: List[Int]
                      )

  implicit val timetableFormat: OFormat[Timetable] = Json.format[Timetable]


}
