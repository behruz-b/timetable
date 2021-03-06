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

    case DeleteGroup(id) =>
      deleteGroup(id).pipeTo(sender())

    case GetGroupList =>
     getGroupList.pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }

  private def addGroup(groupData: Group): Future[Either[String,String]] = {
    (for {
      response <- groupDao.findGroup(groupData.name, groupData.direction)
    } yield response match {
      case Some(group) =>
        Future.successful(Left(group.name + " guruhi dars jadvalida mavjud!"))
      case None =>
        groupDao.addGroup(groupData)
        Future.successful(Right(groupData.name + " guruhi dars jadvalida qo'shildi"))
    }).flatten
  }

  private def updateGroup(group: Group): Future[Option[Int]] = {
    for {
      selectedGroup <- groupDao.getGroupById(group.id)
      updatedGroup = selectedGroup.get.copy(id = group.id, name = group.name, direction = group.direction)
      update <- groupDao.update(updatedGroup)
      response = selectedGroup.get.id
    } yield response
  }

  private def deleteGroup(id: Int): Future[Int] = {
    groupDao.delete(id)
    Future(id)
  }

  private def getGroupList: Future[Seq[Group]] = {
    groupDao.getGroupList
  }
}
