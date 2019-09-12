package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.TeacherProtocol.Teacher
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.Future


trait TeacherComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class TeachersTable(tag: Tag) extends Table[Teacher](tag, "Teachers") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("firstName")

    def lastName = column[String]("lastName")

    def tSubject = column[String]("tSubject")

    def department = column[String]("department")

    def * = (id.?, firstName, lastName, tSubject, department) <> (Teacher.tupled, Teacher.unapply _)
  }

}

@ImplementedBy(classOf[TeacherDaoImpl])
trait TeacherDao {
  def addTeacher(teacherData: Teacher): Future[Int]
}

@Singleton
class TeacherDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends TeacherDao
    with TeacherComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val teachers = TableQuery[TeachersTable]

  override def addTeacher(teacherData: Teacher): Future[Int] = {
    db.run {
      (teachers returning teachers.map(_.id)) += teacherData
    }
  }
}
