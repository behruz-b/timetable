package protocols

import play.api.libs.json.{Json, OFormat}

object TeacherProtocol {

  case object GetTeacherList

  case class AddTeacher(teacher: Teacher)

  case class Teacher(id: Option[Int] = None,
                     fullName: String,
                     tSubject: List[String],
                     department: String
                    )

  implicit val teacherFormat: OFormat[Teacher] = Json.format[Teacher]


}
