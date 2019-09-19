package protocols

import play.api.libs.json.{Json, OFormat}

object SubjectProtocol {

  case object GetSubjectLink

  case class Room(id: Int, numberRoom: Int)

  implicit val roomFormat: OFormat[Room] = Json.format[Room]

  val roomList = Seq(
      Room(1, 101),
      Room(2, 102),
      Room(3, 103),
      Room(4, 104),
      Room(5, 105),
      Room(6, 106),
      Room(7, 107),
      Room(8, 108),
      Room(9, 109),
  )

  case class AddSubject(subjects: Subject)

  case class Subject(id: Option[Int] = None,
                     name: String,
                     numberClassRoom: Int,
                    )

  implicit val subjectFormat: OFormat[Subject] = Json.format[Subject]


}
