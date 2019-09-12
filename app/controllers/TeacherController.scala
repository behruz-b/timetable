package controllers

import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.mvc._
import views.html._

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext

@Singleton
class TeacherController @Inject()(val controllerComponents: ControllerComponents,
                                  teachersTemplate: teachers,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(teachersTemplate())
  }

//  def subjectPost: Action[JsValue] = Action.async(parse.json) { implicit request => {
//    val name = (request.body \ "name").as[String]
//    val numberClassRoom = (request.body \ "numberClassRoom").as[String].toInt
//    (subjectManager ? AddSubject(Subject(None, name, numberClassRoom))).mapTo[Int].map { pr =>
//      Ok(Json.toJson(s"you successful added: $pr"))
//    }
//  }
//  }

}
