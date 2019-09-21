package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.TeacherDao
import javax.inject.Inject
import play.api.Environment
import protocols.TeacherProtocol.{AddTeacher, GetTeacherList, Teacher}

import scala.concurrent.ExecutionContext
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

    case GetTeacherList =>
//      getTeacherList.pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTeacher(teacherData: Teacher) = {
    teacherDao.addTeacher(teacherData)
  }

//  private def getTeacherList = {
//    teacherDao.getTeacher
//  }

}
