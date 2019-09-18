package controllers


import protocols.SubjectProtocol.{AddSubject, Subject}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import views.html._

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext

@Singleton
class SubjectController @Inject()(val controllerComponents: ControllerComponents,
                                  @Named("subject-manager") val subjectManager: ActorRef,
                                  subjectTemplate: subject,
                                 )
                                 (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index: Action[AnyContent] = Action {
    Ok(subjectTemplate())
  }


  def subjectPost: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val name = (request.body \ "name").as[String]
    (subjectManager ? AddSubject(Subject(None, name))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }

}