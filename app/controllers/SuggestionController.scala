package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, _}
import protocols.SuggestionProtocol.{AddSuggestion, GetSuggestionList, Suggestion}
import views.html._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class SuggestionController @Inject()(val controllerComponents: ControllerComponents,
                                     @Named("suggestion-manager") val suggestionManager: ActorRef,
                                     suggestionTemplate: suggestion_table.suggestion_form,
                                     dashboardTemplate: suggestion_table.suggestion_dashboard,
                                    )
                                    (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  val LoginSessionKey = "login.key"

  def index: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map { _ =>
      Ok(suggestionTemplate(true))
    }.getOrElse {
      Unauthorized
    }
  }

  def dashboard: Action[AnyContent] = Action { implicit request =>
    request.session.get(LoginSessionKey).map { _ =>
      Ok(dashboardTemplate(true))
    }.getOrElse {
      Unauthorized
    }
  }

  def addSuggestion: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val teacherName = (request.body \ "teacherName").as[String]
    val suggestion = (request.body \ "suggestion").as[String]
    (suggestionManager ? AddSuggestion(Suggestion(None, teacherName, suggestion))).mapTo[Int].map { pr =>
      Ok(Json.toJson(s"you successful added: $pr"))
    }
  }
  }

  def getSortedSuggestion: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val key = (request.body \ "key").as[String]
    if (key == "id") {
      (suggestionManager ? GetSuggestionList).mapTo[Seq[Suggestion]].map {
        suggestion =>
          request.session.get(LoginSessionKey).map { _ =>
            Ok(Json.toJson(suggestion.sortBy(_.id)))
          }.getOrElse {
            Unauthorized
          }
      }
    }
    else if (key == "teacherName") {
      (suggestionManager ? GetSuggestionList).mapTo[Seq[Suggestion]].map {
        suggestion =>
          request.session.get(LoginSessionKey).map { _ =>
            Ok(Json.toJson(suggestion.sortBy(_.teacherName)))
          }.getOrElse {
            Unauthorized
          }
      }
    }
    else {
      (suggestionManager ? GetSuggestionList).mapTo[Seq[Suggestion]].map {
        suggestion =>
          request.session.get(LoginSessionKey).map { _ =>
            Ok(Json.toJson(suggestion.sortBy(_.id)))
          }.getOrElse {
            Unauthorized
          }
      }
    }
  }
  }

}