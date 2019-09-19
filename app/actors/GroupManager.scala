package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.GroupDao
import javax.inject.Inject
import play.api.Environment
import protocols.GroupProtocol.{AddGroup, Group}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class GroupManager @Inject()(val environment: Environment,
                             groupDao: GroupDao
                            )
                            (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddGroup(group) =>
      addGroup(group).pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }

  private def addGroup(groupData: Group) = {
    groupDao.addGroup(groupData).map { data =>
      data
    }
  }
}
