package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.GroupDao
import javax.inject.Inject
import play.api.Environment
import protocols.GroupProtocol._

import scala.concurrent.{ExecutionContext, Future}
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

    case UpdateGroup(group) =>
      updateGroup(group).pipeTo(sender())

    case GetGroupList =>
     getGroupList.pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }

  private def addGroup(groupData: Group) = {
    groupDao.addGroup(groupData)
  }

  private def updateGroup(group: Group): Future[Int] = {
    for {
      selectedGroup <- groupDao.getGroupById(group.id)
      updatedGroup = selectedGroup.get.copy(id = group.id, name = group.name, direction = group.direction)
      response <- groupDao.update(updatedGroup)
    } yield response
  }

  private def getGroupList = {
    groupDao.getGroupList
  }
}
