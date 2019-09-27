package telegrambot

import scalaj.http.{Http, HttpOptions}

object SendToServer {
  def callApiAndSendMsg(text: String): String = {
    Http(s"http://localhost:9005/text").postData(text)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString.body.mkString("") //.replaceAll("\\s+", " ").split('\n').map(_.trim.filter(_ >= ' ')).mkString(" ")
  }
}
