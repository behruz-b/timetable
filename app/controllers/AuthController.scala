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

  def loginPost = Action { implicit request =>
    val formParams = request.body.asFormUrlEncoded
    val login = formParams.get("login").headOption
    val password = formParams.get("pass").headOption
    val authByLoginAndPwd = usersList.exists(user => user.login == login.getOrElse("") && user.password == password.getOrElse("") )
    if (authByLoginAndPwd) {
      Redirect(routes.TimetableController.dashboard()).addingToSession(LoginSessionKey -> login.getOrElse(""))
    } else {
      Redirect(routes.AuthController.index()).flashing("error" -> "Your login or password is incorrect.")
    }
  }

  def logout = Action { implicit request => {
    Redirect(routes.AuthController.index()).withSession(
      request.session - LoginSessionKey
    )
  }}
}
