package controllers

import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import views.html._
import views.html.timetable._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt


object AuthController {

  case class User(login: String, password: String)

  var usersList = List(
    User("admin", "admin123"),
  )
}


class AuthController @Inject()(val controllerComponents: ControllerComponents,
                               loginT: login,
                               timetable: timeTable
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"
  import AuthController._

  def index: Action[AnyContent] = Action {  implicit request: RequestHeader => {
      try{
        val result= request.session.get(LoginSessionKey)
        if(!result.isEmpty){
          Ok(timetable())
        }
        else
          Ok(loginT())
      }catch{
        case e:Exception=>
          println(e.toString)
          Redirect("/").withNewSession
      }
  } }

  def loginPost = Action(parse.json) { implicit request => {
    val login = (request.body \ "login").as[String]
    val password = (request.body \ "password").as[String]
    val authByLoginAndPwd = usersList.exists(user => user.login == login && user.password == password )
    if (!authByLoginAndPwd) {
      Ok(Json.toJson("Your login or password is incorrect."))
    } else {
      Redirect("/timetable/dashboard").addingToSession(LoginSessionKey -> login)
    }
  }}

  def logout = Action { implicit request => {
    Redirect(routes.AuthController.index()).withSession(
      request.session - LoginSessionKey
    )
  }}
}
