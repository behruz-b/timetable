package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.SubjectDao
import javax.inject.Inject
import play.api.Environment
import protocols.SubjectProtocol._

import scala.concurrent.{ExecutionContext, Future}
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

    case UpdateSubject(subject) =>
      updateSubject(subject).pipeTo(sender())

    case DeleteSubject(id) =>
      deleteSubject(id).pipeTo(sender())

    case GetSubjectList =>
      getSubjectList.pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }


  private def updateSubject(subject: Subject): Future[Option[Int]] = {
    for {
      selectedSubject <- subjectDao.getSubjectById(subject.id)
      updatedSubject = selectedSubject.get.copy(id = subject.id, name = subject.name)
      update <- subjectDao.update(updatedSubject)
      response = selectedSubject.get.id
    } yield response
  }

  private def addSubject(subjectData: Subject): Future[Int] = {
    subjectDao.addSubject(subjectData)
  }

  private def deleteSubject(id: Int): Future[Int] = {
    subjectDao.delete(id)
    Future(id)
  }

  private def getSubjectList: Future[Seq[Subject]] = {
    subjectDao.getSubjectList
  }


}
