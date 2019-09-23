package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.TimetableDao
import javax.inject.Inject
import play.api.Environment
import protocols.TimetableProtocol.{AddTimetable, GetTimetableList, Timetable}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class TimetableManager @Inject()(val environment: Environment,
                                 timetableDao: TimetableDao
                                )
                                (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddTimetable(subject) =>
      addTimetable(subject).pipeTo(sender())

    case GetTimetableList =>
      getTimetableList.pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTimetable(timetableData: Timetable) = {
    timetableDao.addTimetable(timetableData)
  }

  private def getTimetableList = {
    timetableDao.getTimetables
  }

}
