package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.TeacherProtocol._
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.Future


trait TeacherComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  //  implicit val stringListType = new SimpleArrayJdbcType[String]("tSubject").to(_.toList)

  class TeachersTable(tag: Tag) extends Table[Teacher](tag, "Teachers") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def fullName = column[String]("fullName")

    def tSubject = column[String]("tSubject")

    def department = column[String]("department")

    def * = (id.?, fullName, tSubject, department) <> (Teacher.tupled, Teacher.unapply _)
  }

}

@ImplementedBy(classOf[TeacherDaoImpl])
trait TeacherDao {
  def addTeacher(teacherData: Teacher): Future[Int]

  def getTeachersByTS(tSubject: String): Future[Seq[Teacher]]

  def getTeachers: Future[Seq[Teacher]]
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

  override def getTeachers: Future[Seq[Teacher]] = {
    db.run {
      teachers.sortBy(_.id).result
    }
  }

  override def getTeachersByTS(subject: String): Future[Seq[Teacher]] = {
    db.run {
      teachers.filter(_.tSubject === subject).sortBy(_.id).result
    }
  }
}
