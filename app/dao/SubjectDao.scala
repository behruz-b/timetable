package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import protocols.SubjectProtocol.Subject
import utils.Date2SqlDate


import scala.concurrent.Future


trait SubjectComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class SubjectTable(tag: Tag) extends Table[Subject](tag, "Subject") with Date2SqlDate  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id.?, name) <> (Subject.tupled, Subject.unapply _)
  }
}

@ImplementedBy(classOf[SubjectDaoImpl])
trait SubjectDao {
  def addSubject(subjectData: Subject): Future[Int]
}

@Singleton
class SubjectDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends SubjectDao
    with SubjectComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val subjects = TableQuery[SubjectTable]

  override def addSubject(subjectData: Subject): Future[Int] = {
    db.run {
      (subjects returning subjects.map(_.id)) += subjectData
    }
  }
}