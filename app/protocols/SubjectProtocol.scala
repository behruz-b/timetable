package protocols

import play.api.libs.json.{Json, OFormat}

object SubjectProtocol {

  case object GetSubjectList

  case class Room(id: Int, numberRoom: Int)

  implicit val roomFormat: OFormat[Room] = Json.format[Room]

  val roomList = Seq(
    Room(1, 302), Room(2, 303), Room(3, 308), Room(4, 310), Room(5, 311), Room(6, 312),
    Room(7, 313), Room(8, 314), Room(9, 315), Room(10, 316), Room(11, 319), Room(12, 321), Room(13, 325),

    Room(14, 201), Room(15, 202), Room(16, 206), Room(17, 207), Room(18, 209), Room(19, 212), Room(20, 213),
    Room(21, 214), Room(22, 215), Room(23, 216), Room(24, 217),

    Room(25, 102), Room(26, 106), Room(27, 108), Room(28, 109), Room(29, 114), Room(30, 118)
  )

  case class AddSubject(subjects: Subject)

  case class Subject(id: Option[Int] = None,
                     name: String,
                     numberClassRoom: Int,
                    )

  implicit val subjectFormat: OFormat[Subject] = Json.format[Subject]


}
