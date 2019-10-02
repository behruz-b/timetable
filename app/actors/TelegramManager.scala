package actors

import actors.TelegramManager.RunBot
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import javax.inject.Inject
import play.api.{Configuration, Environment}
import telegrambot.TelegramBot

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object TelegramManager {
  case object RunBot
}

class TelegramManager @Inject()(val environment: Environment,
                                val configuration: Configuration)
                               (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  val botUsername: String = configuration.get[String]("bot-username")
  val botToken: String = configuration.get[String]("bot-token")
  val httpLink: String = configuration.get[String]("http-link")

//  UNCOMMIT to run telegram bot

  override def preStart() {
    log.error("Telegram bot started...")
    self ! RunBot
  }


  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case RunBot =>
      runTelegram()

    case _ => log.info(s"received unknown message")
  }

  private def runTelegram() = {
    new TelegramBot().runTelegramJava(botUsername, botToken, httpLink)
  }

}
