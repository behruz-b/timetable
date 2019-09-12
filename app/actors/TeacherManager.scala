package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.SubjectDao
import javax.inject.Inject
import play.api.Environment

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class TeacherManager @Inject()(val environment: Environment,
                               subjectDao: SubjectDao
                              )
                              (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
//    case AddTeacher(subject) =>
//      addTeacher(subject).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

//  private def addTeacher(teacherData: Teacher) = {
//    teacherDao.addTeacher(teacherData).map { data =>
//      data
//    }
//  }

}