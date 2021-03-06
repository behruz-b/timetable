package protocols

import play.api.libs.json.{Json, OFormat}
import protocols.TeacherProtocol.Teacher

object GroupProtocol {

  case class AddGroup(groupData: Group)

  case class UpdateGroup(group: Group)

  case class DeleteGroup(id: Int)

  case object GetGroupList

  case class Direction(id: Int, nameDirections: String)

  implicit val directionFormat: OFormat[Direction] = Json.format[Direction]

  val directionsList = Seq(
    Direction(1, "Axborot Xavfsizligi"),
    Direction(2, "Dasturiy Injineringi"),
    Direction(3, "Kompyuter Injeneringi"),
    Direction(4, "AT-Servise"),
    Direction(5, "Telekommunikatsiya"),
    Direction(6, "Kasb-Ta`limi"),
    Direction(7, "-")
  )

  case class Group(id: Option[Int] = None,
                   name: String,
                   direction: String,
                   count: Option[Int] = None
                  )

  implicit val groupFormat: OFormat[Group] = Json.format[Group]


  case class Room(id: Option[Int] = None,
                  number: String,
                  place: Option[Int] = None,
                  )

  implicit val roomFormat: OFormat[Room] = Json.format[Room]

}
