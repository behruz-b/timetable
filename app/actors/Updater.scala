package actors

import actors.Updater.{RunUpdaterRooms, RunUpdaterSpecPartForAlternation, RunUpdaterSpecPartForLaboratory}
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

  case object RunUpdaterSpecPartForAlternation

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
  val isUpdateAlternation = configuration.get[Boolean]("update-timetable-specPartJson-for-alternation")

  override def preStart() {
    if (isLaboratory) {
      self ! RunUpdaterSpecPartForLaboratory
    }
    if (isUpdateRoom) {
      self ! RunUpdaterRooms
    }
    if (isUpdateAlternation) {
      self ! RunUpdaterSpecPartForAlternation
    }
  }

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunUpdaterSpecPartForLaboratory =>
      updaterSpecPartForLaboratory()

    case RunUpdaterRooms =>
      updaterRoom()

    case RunUpdaterSpecPartForAlternation =>
      updaterSpecPartForAlternation()

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

  private def updaterSpecPartForAlternation() = {
    log.warning(s"keldi")
    for {
      timetables <- timetableDao.getTimetables
    } yield timetables.foreach { timetableData =>
      if (timetableData.alternation.contains("odd")){
        for {
          findAlternation <- timetableDao.findAlternation(timetableData.groups, timetableData.weekDay,
            timetableData.studyShift, timetableData.couple, Some("even"))
          result <- if (findAlternation.isEmpty)
            Future.successful(Option.empty)
          else timetableDao.findAlternation(timetableData.groups, timetableData.weekDay,
            timetableData.studyShift, timetableData.couple, Some("even"))
        } yield result match {
          case Some(timetable) =>
            log.warning(s"keldi bor: ${timetable.id.get}")
            for {
              selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
              updatedTimetable = selectedTimetable.get.copy(
                specPartJson = Some(Json.toJson(Laboratory(timetable.teachers, timetable.numberRoom),
                  Laboratory(timetableData.teachers, timetableData.numberRoom)))
              )
                update <- timetableDao.update(updatedTimetable)
            } yield update
            timetableDao.delete(timetable.id.get)
          case None =>
            for {
              selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
              updatedTimetable = selectedTimetable.get.copy(
                specPartJson = Some(Json.toJson(Laboratory(timetableData.teachers, timetableData.numberRoom)))
              )
              update <- timetableDao.update(updatedTimetable)
            } yield update
        }
      }
      else if (timetableData.alternation.contains("even")){
        for {
          findAlternation <- timetableDao.findAlternation(timetableData.groups, timetableData.weekDay,
            timetableData.studyShift, timetableData.couple, Some("odd"))
          result <- if (findAlternation.isEmpty)
            Future.successful(Option.empty)
          else timetableDao.findAlternation(timetableData.groups, timetableData.weekDay,
            timetableData.studyShift, timetableData.couple, Some("odd"))
        } yield result match {
          case Some(timetable) =>
            log.warning(s"keldi bor2: ${timetable.id.get}")
            for {
              selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
              updatedTimetable = selectedTimetable.get.copy(
                specPartJson = Some(Json.toJson(Laboratory(timetable.teachers, timetable.numberRoom),
                  Laboratory(timetableData.teachers, timetableData.numberRoom)))
              )
              update <- timetableDao.update(updatedTimetable)
            } yield update
            timetableDao.delete(timetable.id.get)
          case None =>
            for {
              selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
              updatedTimetable = selectedTimetable.get.copy(
                specPartJson = Some(Json.toJson(Laboratory(timetableData.teachers, timetableData.numberRoom)))
              )
              update <- timetableDao.update(updatedTimetable)
            } yield update
        }
      }

    }
  }

  private def updaterSpecPartForLaboratory() = {
    for {
      timetables <- timetableDao.getTimetables
    } yield timetables.foreach { timetableData =>
      if (timetableData.typeOfLesson == "Laboratory") {
        log.warning(s"time: ${timetableData.id}")
        for {
          conflicts <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
            timetableData.numberRoom, timetableData.studyShift)
          response <- if (conflicts.isEmpty)
            Future.successful(Option.empty)
          else timetableDao.findGroup(timetableData.weekDay, timetableData.couple,
            timetableData.studyShift, timetableData.groups, timetableData.subjectId, timetableData.typeOfLesson)
        } yield response match {
          case Some(timetable) =>
            log.warning(s"Some: ${timetableData.id}")
            if (
              timetable.teachers != timetableData.teachers &&
                timetable.numberRoom != timetableData.numberRoom
            ) {
              for {
                selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
                updatedTimetable = selectedTimetable.get.copy(
                  specPartJson = Some(Json.toJson(Laboratory(timetable.teachers, timetable.numberRoom),
                    Laboratory(timetableData.teachers, timetableData.numberRoom)))
                )
                _=log.warning(s"update: ${timetableData.id}")
                update <- timetableDao.update(updatedTimetable)
              } yield update
              println(s"delete: ${timetable.id}")
              timetableDao.delete(timetable.id.get)

            }
          case None =>
            log.warning(s"none: ${timetableData.id}")
            for {
              selectedTimetable <- timetableDao.getTimetableById(timetableData.id)
              updatedTimetable = selectedTimetable.get.copy(
                specPartJson = Some(Json.toJson(Laboratory(timetableData.teachers, timetableData.numberRoom)))
              )
              update <- timetableDao.update(updatedTimetable)
            } yield update
        }
      }
    }
  }
}