package dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.TimetableProtocol.Timetable
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.{ExecutionContext, Future}


trait TimetableComponent extends SubjectComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._


  class TimetablesTable(tag: Tag) extends Table[Timetable](tag, "Timetables") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def studyShift = column[String]("studyShift")

    def weekDay = column[String]("weekDay")

    def couple = column[String]("couple")

    def typeOfLesson = column[String]("typeOfLesson")

    def groups = column[String]("groups")

    def divorce = column[String]("divorce")

    def subjectId = column[Int]("subjectId")

    def teachers = column[String]("teachers")

    def numberRoom = column[Int]("numberRoom")

    def specPart = column[String]("specPart")

    def * = (id.?, studyShift, weekDay, couple, typeOfLesson, groups, divorce, subjectId, teachers, numberRoom, specPart.?) <> (Timetable.tupled, Timetable.unapply _)
  }

}

@ImplementedBy(classOf[TimetableDaoImpl])
trait TimetableDao {
  def addTimetable(timetableData: Timetable): Future[Int]

  def getTimetables: Future[Seq[Timetable]]

  def update(timetable: Timetable): Future[Int]

  def getTimetableById(id: Option[Int]): Future[Option[Timetable]]

  def getTimetablesByTeacher(teacher: String): Future[Seq[Timetable]]

  def getBusyRoom(weekDay: String, couple: String): Future[Seq[Timetable]]

  def getTimetableByGroup(weekDay: String, group: String): Future[Seq[Timetable]]

  def getTimetableByGr(group: String): Future[Seq[Timetable]]
}


@Singleton
class TimetableDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                 val actorSystem: ActorSystem)
                                (implicit val ec: ExecutionContext)
  extends TimetableDao
    with TimetableComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val timetables = TableQuery[TimetablesTable]
  val subjects = TableQuery[SubjectTable]
  implicit val materializer = ActorMaterializer()(actorSystem)

  override def addTimetable(timetableData: Timetable): Future[Int] = {
    db.run {
      (timetables returning timetables.map(_.id)) += timetableData
    }
  }

  override def getTimetables: Future[Seq[Timetable]] = {
      val query =  timetables.joinLeft(subjects).on(_.subjectId === _.id)
      db.run(query.result).map { r =>
        r.groupBy(_._1.id).map {case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
  }}

  override def update(timetable: Timetable): Future[Int] = {
    db.run(timetables.filter(_.id === timetable.id).update(timetable))
  }

  override def getTimetableById(id: Option[Int]): Future[Option[Timetable]] = {
    db.run(timetables.filter(_.id === id).result.headOption)
  }

  override def getTimetablesByTeacher(teacher: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data => data.teachers === teacher).result)
  }

  override def getTimetableByGr(group: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(_.groups === group).result)
  }

  override def getBusyRoom(weekDay: String, couple: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data => data.couple === couple && data.weekDay === weekDay).result)
  }

  override def getTimetableByGroup(weekDay: String, group: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data => data.groups === group && data.weekDay === weekDay).result)
  }
}
