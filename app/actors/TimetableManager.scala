package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.{GroupDao, TimetableDao}
import javax.inject.Inject
import org.checkerframework.checker.units.qual.s
import play.api.Environment
import protocols.SubjectProtocol._
import protocols.TimetableProtocol._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class TimetableManager @Inject()(val environment: Environment,
                                 timetableDao: TimetableDao,
                                 groupDao: GroupDao
                                )
                                (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case AddTimetable(subject) =>
      addTimetable(subject).pipeTo(sender())

    case UpdateTimetable(timetable) =>
      updateTimetable(timetable).pipeTo(sender())

    case GetTimetableList =>
      getTimetableList.pipeTo(sender())

    case DeleteTimetable(id) =>
      deleteTimetable(id).pipeTo(sender())

    case t: GetTimetableByGroup =>
      getTimetableByGroup(t.getText).pipeTo(sender())

    case t: GetTimetableForTeacher =>
      getTimetableForTeacher(t.getText).pipeTo(sender())

    case GetTimetableByGr(getText) =>
      getTimetableByGr(getText).pipeTo(sender())

    case t: TimetableForGroup =>
      timetableForGroup(t.group).pipeTo(sender())

    case GetEmptyRoomByCouple(presentCouple) =>
      GetEmptyRoomByCouple(presentCouple).pipeTo(sender())

    case TeacherName(name) =>
      teacherName(name).pipeTo(sender())

    case t: GetTTeacher =>
      getTTeacher(t.teacher).pipeTo(sender())

    case _ => log.info(s"received unknown message")

  }

  private def addTimetable(timetableData: Timetable): Future[Int] = {
    timetableDao.addTimetable(timetableData)
  }

  private def deleteTimetable(id: Int): Future[Int] = {
    timetableDao.delete(id)
  }

  private def updateTimetable(timetable: Timetable): Future[Int] = {
    for {
      selectedTimetable <- timetableDao.getTimetableById(timetable.id)
      updatedTimetable = selectedTimetable.get.copy(
        id = timetable.id,
        studyShift = timetable.studyShift,
        weekDay = timetable.weekDay,
        couple = timetable.couple,
        typeOfLesson = timetable.typeOfLesson,
        groups = timetable.groups,
        divorce = timetable.divorce,
        subjectId = timetable.subjectId,
        teachers = timetable.teachers,
        numberRoom = timetable.numberRoom
      )
      response <- timetableDao.update(updatedTimetable)
    } yield response
  }


  private def getTimetableList: Future[Seq[Timetable]] = {
    timetableDao.getTimetables
  }

  private def teacherName(teacherName: String): Future[Seq[Timetable]] = {
    timetableDao.getTimetablesByTeacher(teacherName)
  }

  private def GetEmptyRoomByCouple(presentCouple: GetEmptyRoom): Future[Seq[String]] = {
    for {
      presentLessons <- timetableDao.getBusyRoom(presentCouple.weekDay, presentCouple.couple)
    } yield roomList.map(_.numberRoom).diff(presentLessons.map(_.numberRoom))
  }

  private def getTimetableByGroup(getText: GetText) = {
    updateCount(getText.group)
    for {
      response <- timetableDao.getTimetableByGroup(getText.weekDay, getText.group)
    } yield response.map { timetable =>
      val trNameDay = timetable.weekDay match {
        case "Monday" => "Dushanba"
        case "Tuesday" => "Seshanba"
        case "Wednesday" => "Chorshanba"
        case "Thursday" => "Payshanba"
        case "Friday" => "Juma"
        case "Saturday" => "Shanba"
      }
      val trStudyShift = timetable.studyShift match {
        case "Afternoon" => "2 - Smena"
        case "Morning" => "1 - Smena"
      }
      val trCouple = timetable.couple match {
        case "couple 1" => "1 - Juftlik"
        case "couple 2" => "2 - Juftlik"
        case "couple 3" => "3 - Juftlik"
        case "couple 4" => "4 - Juftlik"
      }
      val trTypeLesson = timetable.typeOfLesson match {
        case "Laboratory" => "Laboratoriya"
        case "Practice" => "Amaliyot"
        case "Lecture" => "Ma'ruza"
      }
      val timetableMapped = timetable.copy(weekDay = trNameDay, studyShift = trStudyShift, couple = trCouple, typeOfLesson = trTypeLesson)
      timetableMapped
    }
  }

  private def getTimetableForTeacher(getText: GetText) = {
    for {
      response <- timetableDao.getTByTeacherAndWeekday(getText.weekDay, getText.group)
    } yield response.map { timetable =>
      val trNameDay = timetable.weekDay match {
        case "Monday" => "Dushanba"
        case "Tuesday" => "Seshanba"
        case "Wednesday" => "Chorshanba"
        case "Thursday" => "Payshanba"
        case "Friday" => "Juma"
        case "Saturday" => "Shanba"
      }
      val trStudyShift = timetable.studyShift match {
        case "Afternoon" => "2 - Smena"
        case "Morning" => "1 - Smena"
      }
      val trCouple = timetable.couple match {
        case "couple 1" => "1 - Juftlik"
        case "couple 2" => "2 - Juftlik"
        case "couple 3" => "3 - Juftlik"
        case "couple 4" => "4 - Juftlik"
      }
      val trTypeLesson = timetable.typeOfLesson match {
        case "Laboratory" => "Laboratoriya"
        case "Practice" => "Amaliyot"
        case "Lecture" => "Ma'ruza"
      }
      val timetableMapped = timetable.copy(weekDay = trNameDay, studyShift = trStudyShift, couple = trCouple, typeOfLesson = trTypeLesson)
      timetableMapped

    }
  }

  private def timetableForGroup(group: String) = {
    for {
      response <- timetableDao.getTimetableByGr(group)
    } yield response.map { timetable =>
      val trStudyShift = timetable.studyShift match {
        case "Afternoon" => "2 - Smena"
        case "Morning" => "1 - Smena"
      }
      val trCouple = timetable.couple match {
        case "couple 1" => "1 - Juftlik"
        case "couple 2" => "2 - Juftlik"
        case "couple 3" => "3 - Juftlik"
        case "couple 4" => "4 - Juftlik"
      }
      val trTypeLesson = timetable.typeOfLesson match {
        case "Laboratory" => "Laboratoriya"
        case "Practice" => "Amaliyot"
        case "Lecture" => "Ma'ruza"
      }
      val timetableMapped = timetable.copy(studyShift = trStudyShift, couple = trCouple, typeOfLesson = trTypeLesson)
      timetableMapped

    }
  }

  private def getTTeacher(teacher: String) = {
    for {
      response <- timetableDao.getTimetablesByTeacher(teacher)
    } yield response.map { timetable =>
      val trNameDay = timetable.weekDay match {
        case "Monday" => "Dushanba"
        case "Tuesday" => "Seshanba"
        case "Wednesday" => "Chorshanba"
        case "Thursday" => "Payshanba"
        case "Friday" => "Juma"
        case "Saturday" => "Shanba"
      }
      val trStudyShift = timetable.studyShift match {
        case "Afternoon" => "2 - Smena"
        case "Morning" => "1 - Smena"
      }
      val trCouple = timetable.couple match {
        case "couple 1" => "1 - Juftlik"
        case "couple 2" => "2 - Juftlik"
        case "couple 3" => "3 - Juftlik"
        case "couple 4" => "4 - Juftlik"
      }
      val trTypeLesson = timetable.typeOfLesson match {
        case "Laboratory" => "Laboratoriya"
        case "Practice" => "Amaliyot"
        case "Lecture" => "Ma'ruza"
      }
      val timetableMapped = timetable.copy(weekDay = trNameDay, studyShift = trStudyShift, couple = trCouple, typeOfLesson = trTypeLesson)
      log.warning(s"timetable: $timetableMapped")
      timetableMapped

    }
  }

  private def updateCount(group: String) = {
    for {
      selectedGroup <- groupDao.getGroupByName(group)
      countSearched = selectedGroup.get.count.getOrElse(0) + 1
      updatedGroup = selectedGroup.get.copy(count = Some(countSearched))
      response <- groupDao.update(updatedGroup)
    } yield response
  }

  private def getTimetableByGr(group: String) = {
    for {
      response <- timetableDao.getTimetableByGr(group)
    } yield response
  }
}
