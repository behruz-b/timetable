package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.TeacherDao
import javax.inject.Inject
import play.api.Environment
import protocols.TeacherProtocol._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class TeacherManager @Inject()(val environment: Environment,
                               teacherDao: TeacherDao
                              )
                              (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddTeacher(subject) =>
      addTeacher(subject).pipeTo(sender())

    case UpdateTeacher(teacher) =>
      updateTeacher(teacher).pipeTo(sender())

    case GetTeacherList =>
      getTeacherList.pipeTo(sender())

    case GetTeacherListByTS(tSubject) =>
      getTeacherListByTS(tSubject).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTeacher(teacherData: Teacher) = {
    teacherDao.addTeacher(teacherData)
  }

  private def updateTeacher(teacher: Teacher): Future[Int] = {
    for {
      selectedTeacher <- teacherDao.getTeacherById(teacher.id)
      updatedTeacher = selectedTeacher.get.copy(id = teacher.id)
      response <- teacherDao.update(updatedTeacher)
    } yield response
  }


  private def getTeacherList   = {
    teacherDao.getTeachers
  }

  private def getTeacherListByTS(subject: String)   = {
    teacherDao.getTeachersByTS(subject)
  }

}
