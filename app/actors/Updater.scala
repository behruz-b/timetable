package actors

import actors.Updater.RunUpdater
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import dao.TimetableDao
import javax.inject.Inject
import play.api.Environment
import play.api.libs.json.Json
import protocols.TimetableProtocol.Laboratory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object Updater {

  case object RunUpdater

}

class Updater @Inject()(val environment: Environment,
                        timetableDao: TimetableDao
                       )
                       (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {
//  override def preStart() {
//    self ! RunUpdater
//  }

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunUpdater =>
      log.error(s"RUN:")
      updater()

    case _ => log.info(s"received unknown message")

  }


  private def updater() = {
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