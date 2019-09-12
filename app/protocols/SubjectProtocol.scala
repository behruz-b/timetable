package protocols

import play.api.libs.json.{Json, OFormat}

object SubjectProtocol {

  case class AddSubject(subjects: Subject)

  case class Subject(id: Option[Int] = None,
                   name: String,
                   numberClassRoom: Int,
                   )

  implicit val subjectFormat: OFormat[Subject] = Json.format[Subject]


}
