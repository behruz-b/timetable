package telegrambot

import scala.io.Source

object TelegramCredentials {

  case class TelegramCredentials(username: String, token: String)
  val BotUsername = "bot-username"
  val BotToken = "bot-token"

  def getTelegramCredentials(filePath: String): TelegramCredentials = {
    val lines = Source.fromFile(filePath)
    val readyList = lines.getLines()
      .map(_.trim)
      .filter(line => !line.startsWith("#") && !line.isEmpty)
      .to[List]
    lines.close()
    val credentials =  readyList.filter(l => l.startsWith(BotUsername) || l.startsWith(BotToken)).map(_.replaceAllLiterally(BotToken, "")
      .replaceAllLiterally(BotUsername, "")
      .replaceAll("[\"=\\s*]", ""))
    TelegramCredentials(credentials.head, credentials.last)
  }

}
