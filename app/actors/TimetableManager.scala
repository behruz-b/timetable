package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.TimetableDao
import javax.inject.Inject
import play.api.Environment
import protocols.TimetableProtocol.{AddTimetable, GetText, GetTimetableByGroup, GetTimetableList, Timetable}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class TimetableManager @Inject()(val environment: Environment,
                                 timetableDao: TimetableDao
                                )
                                (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddTimetable(subject) =>
      addTimetable(subject).pipeTo(sender())

    case GetTimetableList =>
      getTimetableList.pipeTo(sender())

    case GetTimetableByGroup(getText) =>
      getTimetableByGroup(getText).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTimetable(timetableData: Timetable): Future[Int] = {
    timetableDao.addTimetable(timetableData)
  }

  private def getTimetableList: Future[Seq[Timetable]] = {
    timetableDao.getTimetables
  }

  private def getTimetableByGroup(getText: GetText) = {
    (for {
      response <- timetableDao.getTimetableByGroup(getText.weekDay, getText.group).mapTo[Option[Timetable]]
    } yield response match {
      case Some(timetable) =>
        val trNameDay = timetable.weekDay match {
          case  "Monday" => "Dushanba"
          case  "Tuesday" => "Seshanba"
          case  "Wednesday" => "Chorshanba"
          case  "Thursday" => "Payshanba"
          case  "Friday" => "Juma"
          case  "Saturday" => "Shanba"
          case  "Sunday" => "Yakshanba"
        }
        val trStydyShift = timetable.studyShift match {
          case  "Afternoon" => "2 - Smena"
          case  "Morning" => "1 - Smena"
        }
        val trCouple = timetable.couple match {
          case  "couple 1" => "1 - Juftlik"
          case  "couple 2" => "2 - Juftlik"
          case  "couple 3" => "3 - Juftlik"
          case  "couple 4" => "4 - Juftlik"
        }
        val trTypeLesson = timetable.typeOfLesson match {
          case  "Laboratory" => "Laboratoriya"
          case  "Practice" => "Amaliyot"
          case  "Lecture" => "Ma'ruza"
        }
        val timetableMapped = timetable.copy(weekDay = trNameDay, studyShift = trStydyShift, couple = trCouple, typeOfLesson = trTypeLesson)
        Future.successful(
          "Hafta kuni:                  " + timetableMapped.weekDay.toString + "\n" +
          "Guruh:                       " + timetableMapped.groups.toString + "\n" +
          "O'qish vaqti:                " + timetableMapped.studyShift.toString  + "\n" +
          "Juftlik:                     " + timetableMapped.couple.toString  + "\n" +
          "Fan:                         " + timetableMapped.subjectId.toString + "\n" +
          "Mashg'ulot turi              " + timetableMapped.typeOfLesson.toString + "\n" +
          "O'qituvchi:                  " + timetableMapped.teachers.toString  + "\n" +
          "Dars xonasi:                 " + timetableMapped.numberRoom.toString
        )
      case None =>
        Future.successful("this is Group not found")
    }).flatten
  }

}
