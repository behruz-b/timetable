package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.TimetableDao
import javax.inject.Inject
import play.api.Environment
import protocols.TimetableProtocol.{AddTimetable, GetText, GetTimetableByGroup, GetTimetableList, Timetable}

import scala.concurrent.{ExecutionContext, Future}
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

    case GetTimetableByGroup(getText) =>
      getTimetableByGroup(getText).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTimetable(timetableData: Timetable): Future[Int] = {
    timetableDao.addTimetable(timetableData)
  }

  private def getTimetableList: Future[Seq[Timetable]] = {
    timetableDao.getTimetables
  }

  private def getTimetableByGroup(getText: GetText) = {
    (for {
      response <- timetableDao.getTimetableByGroup(getText.weekDay, getText.group)
    } yield response match {
      case Some(isGroup) =>
        Future.successful(isGroup.toString)
      case None =>
        Future.successful("this is no Group")
    }).flatten
  }

}
