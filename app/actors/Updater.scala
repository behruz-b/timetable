package actors

import actors.Updater.RunUpdater
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import dao.TimetableDao
import javax.inject.Inject
import play.api.Environment

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Updater {

  case object RunUpdater

}

class Updater @Inject()(val environment: Environment,
                        timetableDao: TimetableDao
                       )
                       (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {
  override def preStart() {
    self ! RunUpdater
  }

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunUpdater =>
      updater()

    case _ => log.info(s"received unknown message")

  }


  private def updater() = {
    for {
      selectedSubject <- timetableDao.getTT

    } yield selectedSubject
  }

}
