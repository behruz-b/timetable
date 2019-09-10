package controllers

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.mvc._
import views.html._

import scala.concurrent.ExecutionContext

@Singleton
class SubjectController @Inject()(val controllerComponents: ControllerComponents,
                               subjectTemplate: subject,
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  def index: Action[AnyContent] = Action {
    Ok(subjectTemplate())
  }
}