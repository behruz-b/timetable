package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.SubjectDao
import javax.inject.Inject
import play.api.Environment
import protocols.SubjectProtocol.{AddSubject, GetSubjectList, Subject}
import protocols.TeacherProtocol.{Teacher, UpdateTeacher}

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

    case UpdateSubject(teacher) =>
      updateSubject(teacher).pipeTo(sender())

    case GetSubjectList =>
      getSubjectList.pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }


  private def updateSubject(subject: Subject): Future[Int] = {
    for {
      selectedSubject <- subjectDao.getSubjectById(subject.id)
      updatedSubject = selectedSubject.get.copy(id = subject.id)
      response <- subjectDao.update(updatedSubject)
    } yield response
  }

  private def addSubject(subjectData: Subject) = {
    subjectDao.addSubject(subjectData)
  }

  private def getSubjectList = {
    subjectDao.getSubjectList
  }


}
