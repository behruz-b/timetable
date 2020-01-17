package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.GroupProtocol.Room
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.Future


trait RoomComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class RoomTable(tag: Tag) extends Table[Room](tag, "Room") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def number = column[String]("number")

    def place = column[Int]("place")

    def * = (id.?, number, place.?) <> (Room.tupled, Room.unapply _)
  }

}

@ImplementedBy(classOf[RoomDaoImpl])
trait RoomDao {
  def addRoom(data: Room): Future[Int]

  def getRoomList: Future[Seq[Room]]

  def update(data: Room): Future[Int]

}


@Singleton
class RoomDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends RoomDao
    with RoomComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val rooms = TableQuery[RoomTable]

  override def addRoom(data: Room): Future[Int] = {
    db.run {
      logger.warn(s"room: $data")
      (rooms returning rooms.map(_.id)) += data
    }
  }

  override def getRoomList: Future[Seq[Room]] = {
    db.run {
      rooms.result
    }
  }

  override def update(data: Room): Future[Int] = {
    db.run {
      rooms.filter(_.id === data.id).update(data)
    }
  }
}
