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

    case DeleteTeacher(id) =>
      deleteTeacher(id).pipeTo(sender())

    case GetTeacherList =>
      getTeacherList.pipeTo(sender())

    case GetTeacherListByTS(tSubject) =>
      getTeacherListByTS(tSubject).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTeacher(teacherData: Teacher) = {
    teacherDao.addTeacher(teacherData)
  }

  private def updateTeacher(teacher: Teacher): Future[Option[Int]] = {
    for {
      selectedTeacher <- teacherDao.getTeacherById(teacher.id)
      updatedTeacher = selectedTeacher.get.copy(
        id = teacher.id,
        fullName = teacher.fullName,
        tSubject = teacher.tSubject,
        department = teacher.department
      )
      update <- teacherDao.update(updatedTeacher)
      response = selectedTeacher.get.id
    } yield response
  }


  private def deleteTeacher(id: Int): Future[Int] = {
    teacherDao.delete(id)
    Future(id)
  }

  private def getTeacherList   = {
    teacherDao.getTeachers
  }

  private def getTeacherListByTS(subject: String)   = {
    teacherDao.getTeachersByTS(subject)
  }

}
