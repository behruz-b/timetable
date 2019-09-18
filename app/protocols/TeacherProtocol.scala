package protocols

import play.api.libs.json.{Json, OFormat}

object TeacherProtocol {

  case class AddTeacher(teacher: Teacher)

  case class Teacher(id: Option[Int] = None,
                     fullName:String,
                     tSubject: String,
                     department: String
                    )

  implicit val subjectFormat: OFormat[Teacher] = Json.format[Teacher]


}
