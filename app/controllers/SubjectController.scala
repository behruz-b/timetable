package controllers

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import views.html._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubjectController @Inject()(val controllerComponents: ControllerComponents,
                                  subjectTemplate: subject,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  def index: Action[AnyContent] = Action {
    Ok(subjectTemplate())
  }

//  def subjectPost = Action.async(parse.json) { implicit request => {
//    val text1 = (request.body \ "text1").as[String]
//    val text2 = (request.body \ "text2").as[String]
//    Ok()
//  }
//  }

}