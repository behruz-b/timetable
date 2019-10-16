package protocols

import play.api.libs.json.{Json, OFormat}

object SuggestionProtocol {

  case object GetSuggestionList

  case class AddSuggestion(suggestion: Suggestion)

  case class Suggestion(id: Option[Int] = None,
                        teacherName: String,
                        suggestion: String,
                       )

  implicit val suggestionFormat: OFormat[Suggestion] = Json.format[Suggestion]


}
