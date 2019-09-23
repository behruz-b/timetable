package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.TimetableProtocol.Timetable
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.Future


trait TimetableComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  //  implicit val stringListType = new SimpleArrayJdbcType[String]("tSubject").to(_.toList)

  class TimetablesTable(tag: Tag) extends Table[Timetable](tag, "Timetables") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def studyShift = column[String]("studyShift")

    def weekDay = column[String]("weekDay")

    def couple = column[String]("couple")

    def typeOfLesson = column[String]("typeOfLesson")

    def groups = column[List[String]]("groups")

    def subjectId = column[Int]("subjectId")

    def teachers = column[List[String]]("teachers")

    def numberRoom = column[List[Int]]("numberRoom")

    def * = (id.?, studyShift, weekDay, couple, typeOfLesson, groups, subjectId, teachers, numberRoom) <> (Timetable.tupled, Timetable.unapply _)
  }

}

@ImplementedBy(classOf[TimetableDaoImpl])
trait TimetableDao {
  def addTimetable(timetableData: Timetable): Future[Int]

  def getTimetables: Future[Seq[Timetable]]
}


@Singleton
class TimetableDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends TimetableDao
    with TimetableComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val timetables = TableQuery[TimetablesTable]

  override def addTimetable(timetableData: Timetable): Future[Int] = {
    db.run {
      (timetables returning timetables.map(_.id)) += timetableData
    }
  }

  override def getTimetables: Future[Seq[Timetable]] = {
    db.run {
      timetables.sortBy(_.id).result
    }
  }
}
