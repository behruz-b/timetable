package actors

import actors.Updater.{RunUpdaterRooms, RunUpdaterSpecPartForLaboratory}
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import dao.{RoomDao, TimetableDao}
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.{Configuration, Environment}
import protocols.GroupProtocol.Room
import protocols.SubjectProtocol.roomList
import protocols.TimetableProtocol.Laboratory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object Updater {

  case object RunUpdaterSpecPartForLaboratory

  case object RunUpdaterRooms

}

class Updater @Inject()(val environment: Environment,
                        val configuration: Configuration,
                        timetableDao: TimetableDao,
                        roomDao: RoomDao
                       )
                       (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {
  val isLaboratory = configuration.get[Boolean]("update-timetable-specPartJson-for-Laboratory")
  val isUpdateRoom = configuration.get[Boolean]("update-rooms")

  override def preStart() {
    log.warning(s"preStart")
    if (isLaboratory) {
      log.warning(s"preStartLab: $isLaboratory")
      self ! RunUpdaterSpecPartForLaboratory
    }
    if (isUpdateRoom) {
      log.warning(s"preStartRoom: $isUpdateRoom")
      self ! RunUpdaterRooms
    }
  }

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunUpdaterSpecPartForLaboratory =>
      updaterSpecPartForLaboratory()

    case RunUpdaterRooms =>
      updaterRoom()

    case _ => log.info(s"received unknown message")

  }

  private def updaterRoom() = {
    for {
      room <- Future(roomList)
      roomInDB <- roomDao.getRoomList
    } yield {
      if(roomInDB.isEmpty){
        room.map{ r =>
        roomDao.addRoom(Room(number = r.numberRoom))
      }
      } else{
        room.map{ r =>
          roomDao.update(Room(Some(r.id), r.numberRoom, r.place))
        }
      }
    }
  }

  private def updaterSpecPartForLaboratory() = {
    for {
      timetables <- timetableDao.getTimetables
    } yield timetables.foreach { timetableData =>
      if (timetableData.typeOfLesson == "Laboratory") {
        for {
          conflicts <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
            timetableData.numberRoom, timetableData.studyShift)
          response <- if (conflicts.isEmpty)
            Future.successful(Option.empty)
          else timetableDao.findGroup(timetableData.weekDay, timetableData.couple,
            timetableData.studyShift, timetableData.groups, timetableData.subjectId, timetableData.typeOfLesson)
        } yield response match {
          case Some(timetable) =>
            if (
              timetable.teachers != timetableData.teachers &&
                timetable.numberRoom != timetableData.numberRoom
            ) {
              for {
                selectedTimetable <- timetableDao.getTimetableById(timetable.id)
                updatedTimetable = selectedTimetable.get.copy(
                  specPartJson = Some(Json.toJson(Laboratory(timetable.teachers, timetable.numberRoom),
                    Laboratory(timetableData.teachers, timetableData.numberRoom)))
                )
                update <- timetableDao.update(updatedTimetable)
              } yield update
            }
          case None =>
        }
      }
    }
  }
}