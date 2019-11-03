package protocols

import play.api.libs.json.{Json, OFormat}

object TeacherProtocol {

  case class GetTeacherListByTS(tSubject: String)

  implicit val getTeacherListByTSFormat: OFormat[GetTeacherListByTS] = Json.format[GetTeacherListByTS]

  case class UpdateTeacher(teacher: Teacher)

  case class DeleteTeacher(id: Int)

  case object GetTeacherList

  case class Department(id: Int, department: String)

  implicit val departmentFormat: OFormat[Department] = Json.format[Department]

  val directionsList = Seq(
    Department(1, "Axborot texnologiyalari kafedrasi "),
    Department(2, "Dasturiy injiniring kafedrasi "),
    Department(3, "Gumanitar va ijtimoiy fanlar kafedrasi"),
    Department(4, "Telekommunikatsiya injiniringi kafedrasi"),
    Department(5, "Tabiiy va umumkasbiy fanlar kafedrasi "),
    Department(6, "Axborot talim texnologiyalari kafedrasi")
  )

  case class AddTeacher(teacher: Teacher)

  case class Teacher(id: Option[Int] = None,
                     fullName: String,
                     tSubject: String,
                     department: String
                    )

  implicit val teacherFormat: OFormat[Teacher] = Json.format[Teacher]


}
