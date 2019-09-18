package protocols

import play.api.libs.json.{Json, OFormat}

object GroupProtocol {
    case class AddGroup(groupData: Group)

    case class Group(id: Option[Int] = None,
                       name: String,
                      )

    implicit val subjectFormat: OFormat[Group] = Json.format[Group]

}
