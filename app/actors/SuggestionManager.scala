package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.SuggestionDao
import javax.inject.Inject
import play.api.Environment
import protocols.SuggestionProtocol._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class SuggestionManager @Inject()(val environment: Environment,
                                  suggestionDao: SuggestionDao
                                 )
                                 (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddSuggestion(suggestion) =>
      addSuggestion(suggestion).pipeTo(sender())

    case DeleteSuggestion(id) =>
      deleteSuggestion(id).pipeTo(sender())

    case GetSuggestionList =>
      getSuggestionList.pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addSuggestion(suggestionData: Suggestion): Future[Int] = {
    suggestionDao.addSuggestion(suggestionData)
  }


  private def deleteSuggestion(id: Int): Future[Int] = {
    suggestionDao.delete(id)
  }

  private def getSuggestionList = {
    suggestionDao.getSuggestionList
  }


}
