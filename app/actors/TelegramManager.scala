package actors

import actors.TelegramManager.RunBot
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import javax.inject.Inject
import play.api.Environment
import telegrambot.TelegramBot

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object TelegramManager {
  case object RunBot
}

class TelegramManager @Inject()(val environment: Environment)
                               (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

//  UNCOMMIT to run telegram bot

//  override def preStart() {
//    self ! RunBot
//  }


  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunBot =>
      runTelegram()

    case _ => log.info(s"received unknown message")
  }

  private def runTelegram() = {
    new TelegramBot().runTelegramJava()
  }

}
