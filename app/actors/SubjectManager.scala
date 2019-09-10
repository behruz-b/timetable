package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.SubjectDao
import javax.inject.Inject
import play.api.Environment
import protocols.SubjectProtocol.{AddSubject, Subject}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class SubjectManager @Inject()(val environment: Environment,
                               subjectDao: SubjectDao
                              )
                              (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddSubject(subject) =>
      addSubject(subject).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addSubject(subjectData: Subject) = {
    subjectDao.addSubject(subjectData).map { data =>
      data
    }
  }

}
