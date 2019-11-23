package dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.JsValue
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

    def numberRoom = column[String]("numberRoom")

    def specPart = column[String]("specPart")

    def flow = column[Boolean]("flow")

    def alternation = column[String]("alternation")

    def specPartJson = column[JsValue]("specPartJson")

    def * = (id.?, studyShift, weekDay, couple, typeOfLesson, groups, divorce, subjectId, teachers, numberRoom, specPart.?, flow, alternation.?, specPartJson.?) <> (Timetable.tupled, Timetable.unapply _)
  }

}

@ImplementedBy(classOf[TimetableDaoImpl])
trait TimetableDao {
  def addTimetable(timetableData: Timetable): Future[Int]

  def getTimetables: Future[Seq[Timetable]]

  def update(timetable: Timetable): Future[Int]

  def delete(id: Int): Future[Int]

  def getTimetableById(id: Option[Int]): Future[Option[Timetable]]

  def getTimetablesByTeacher(teacher: String): Future[Seq[Timetable]]

  def getBusyRoom(weekDay: String, couple: String, studyShift: String): Future[Seq[Timetable]]

  def getTimetableByGroup(weekDay: String, group: String): Future[Seq[Timetable]]

  def getTByTeacherAndWeekday(weekDay: String, teacher: String): Future[Seq[Timetable]]

  def getTimetableByGr(group: String): Future[Seq[Timetable]]

  def findConflicts(weekDay: String, couple: String, numberRoom: String, studyShift: String): Future[Option[Timetable]]

  def findGroup(weekDay: String, couple: String, studyShift: String, group: String, subjectId: Int, typeOfLesson: String): Future[Option[Timetable]]
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

    val query = timetables.joinLeft(subjects).on(_.subjectId === _.id)
    db.run(query.result).map { r =>
      r.groupBy(_._1.id).map { case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
    }
  }

  override def update(timetable: Timetable): Future[Int] = {
    db.run{
      timetables.filter(_.id === timetable.id).update(timetable)
    }
  }

  override def delete(id: Int): Future[Int] = {
    db.run(timetables.filter(_.id === id).delete)
  }

  override def getTimetableById(id: Option[Int]): Future[Option[Timetable]] = {
    db.run(timetables.filter(_.id === id).result.headOption)
  }

  override def getTimetablesByTeacher(teacher: String): Future[Seq[Timetable]] = {
    val query = timetables
      .filter(data => data.teachers === teacher)
      .joinLeft(subjects)
      .on(_.subjectId === _.id)
    db.run(query.result).map { r =>
      r.groupBy(_._1.id).map { case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
    }
  }

  override def getTimetableByGr(group: String): Future[Seq[Timetable]] = {
    val query = timetables
      .filter(_.groups === group)
      .joinLeft(subjects)
      .on(_.subjectId === _.id)
    db.run(query.result).map { r =>
      r.groupBy(_._1.id).map { case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
    }
  }

  override def findConflicts(weekDay: String, couple: String, numberRoom: String, studyShift: String): Future[Option[Timetable]] = {
    db.run{
      timetables.filter(data =>
        data.weekDay === weekDay &&
        data.couple === couple &&
        data.numberRoom === numberRoom &&
        data.studyShift === studyShift
      ).result.headOption
    }
  }

  override def findGroup(weekDay: String, couple: String, studyShift: String, group: String,  subjectId: Int, typeOfLesson: String): Future[Option[Timetable]] = {
    db.run{
      timetables.filter(data =>
        data.weekDay === weekDay &&
        data.couple === couple &&
        data.studyShift === studyShift &&
        data.groups === group &&
        data.subjectId === subjectId &&
        data.typeOfLesson === typeOfLesson
      ).result.headOption
    }
  }

  override def getBusyRoom(weekDay: String, couple: String, studyShift: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data =>
      data.couple === couple &&
      data.weekDay === weekDay &&
      data.studyShift === studyShift
    ).result)
  }

  override def getTimetableByGroup(weekDay: String, group: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data => data.groups === group && data.weekDay === weekDay).result)
    val query = timetables
      .filter(data => data.groups === group && data.weekDay === weekDay)
      .joinLeft(subjects)
      .on(_.subjectId === _.id)
    db.run(query.result).map { r =>
      r.groupBy(_._1.id).map { case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
    }
  }

  override def getTByTeacherAndWeekday(weekDay: String, teacher: String): Future[Seq[Timetable]] = {
    db.run(timetables.filter(data => data.teachers === teacher && data.weekDay === weekDay).result)
    val query = timetables
      .filter(data => data.teachers === teacher && data.weekDay === weekDay)
      .joinLeft(subjects)
      .on(_.subjectId === _.id)
    db.run(query.result).map { r =>
      r.groupBy(_._1.id).map { case (_, tuples) =>
        val (t, s) = tuples.head
        t.copy(specPart = Some(s.get.name))
      }.to[Seq]
    }
  }

}
