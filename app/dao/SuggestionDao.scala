package dao

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import protocols.SuggestionProtocol.Suggestion
import utils.Date2SqlDate


import scala.concurrent.Future


trait SuggestionComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class SuggestionTable(tag: Tag) extends Table[Suggestion](tag, "Suggestion") with Date2SqlDate  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def teacherName = column[String]("teacherName")

    def suggestion = column[String]("suggestion")

    def * = (id.?, teacherName, suggestion) <> (Suggestion.tupled, Suggestion.unapply _)
  }
}

@ImplementedBy(classOf[SuggestionDaoImpl])
trait SuggestionDao {
  def addSuggestion(suggestionData: Suggestion): Future[Int]

  def delete(id: Int): Future[Int]

  def getSuggestionList: Future[Seq[Suggestion]]
}

@Singleton
class SuggestionDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends SuggestionDao
    with SuggestionComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with LazyLogging {

  import utils.PostgresDriver.api._

  val Suggestions = TableQuery[SuggestionTable]

  override def addSuggestion(suggestionData: Suggestion): Future[Int] = {
    db.run {
      (Suggestions returning Suggestions.map(_.id)) += suggestionData
    }
  }

  override def delete(id: Int): Future[Int] = {
    db.run(Suggestions.filter(_.id === id).delete)
  }

  override def getSuggestionList: Future[Seq[Suggestion]] = {
    db.run  (Suggestions.result)
  }
}
