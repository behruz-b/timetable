package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.GroupProtocol.Group
import protocols.TeacherProtocol.Teacher
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.Future


trait GroupComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class GroupTable(tag: Tag) extends Table[Group](tag, "Groups") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def direction = column[String]("direction")

    def * = (id.?, name, direction) <> (Group.tupled, Group.unapply _)
  }

}

@ImplementedBy(classOf[GroupDaoImpl])
trait GroupDao {
  def addGroup(GroupData: Group): Future[Int]

  def update(groupData: Group): Future[Int]

  def getGroupById(id: Option[Int]): Future[Option[Group]]

  def getGroupList: Future[Seq[Group]]
}


@Singleton
class GroupDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends GroupDao
    with GroupComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val groups = TableQuery[GroupTable]

  override def addGroup(groupData: Group): Future[Int] = {
    db.run {
      (groups returning groups.map(_.id)) += groupData
    }
  }

  override def update(groupData: Group): Future[Int] = {
    db.run(groups.filter(_.id === groupData.id).update(groupData))
  }

  override def getGroupById(id: Option[Int]): Future[Option[Group]] = {
    db.run(groups.filter(_.id === id).result.headOption)
  }

  override def getGroupList: Future[Seq[Group]] = {
    db.run  (groups.sortBy(_.id).result)
  }
}
