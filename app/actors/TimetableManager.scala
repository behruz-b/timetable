package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.{GroupDao, TimetableDao}
import javax.inject.Inject
import play.api.Environment
import play.api.libs.json.{JsValue, Json}
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

  private def addTimetable(timetableData: Timetable) = {
    (
      timetableData.alternation match {
        case None =>
          timetableData.typeOfLesson match {
            case "Lecture" =>
              for {
                response <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
              } yield response match {
                case Some(timetable) =>
                  // agar potochni bo`lsa
                  if (timetable.subjectId == timetableData.subjectId &&
                    timetable.teachers == timetableData.teachers &&
                    timetableData.flow == timetable.flow &&
                    timetableData.flow &&
                    timetableData.typeOfLesson == "Lecture"
                  ) {
                    timetableDao.addTimetable(timetableData)
                    Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
                  }
                  else {
                    Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni shu parada " + timetable.teachers +
                      " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                  }

                case None =>
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
            case "Laboratory" =>
              for {
                response <- timetableDao.findGroup(timetableData.weekDay, timetableData.couple,
                  timetableData.studyShift, timetableData.groups, timetableData.subjectId, timetableData.typeOfLesson)
                conflicts <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
                if conflicts.isEmpty
              } yield response match {
                case Some(timetable) =>
                  log.warning(s"megalka emas dars bor")
                  if (timetable.alternation.isEmpty &&
                    timetable.teachers != timetableData.teachers &&
                    timetable.numberRoom != timetableData.numberRoom
                  ) {
                    for {
                      selectedTimetable <- timetableDao.getTimetableById(timetable.id)
                      updatedTimetable = selectedTimetable.get.copy(
                        teachers = Json.toJson(timetable.teachers,timetableData.teachers),
                        numberRoom = Json.toJson(timetable.numberRoom,timetableData.numberRoom)
                      )
                      update <- timetableDao.update(updatedTimetable)
                    } yield update
                    Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
                  } else {
                    Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni " +
                      translateAlternation(timetable.alternation) + " haftada shu parada " +
                      timetable.teachers + " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                  }
                case None =>
                  log.warning(s"megalka emas dars yo`q")
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
            case _ =>
              for {
                response <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
              } yield response match {
                case Some(timetable) =>
                  Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni shu parada " + timetable.teachers +
                    " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                case None =>
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
          }

        case _ =>
          timetableData.typeOfLesson match {
            case "Lecture" =>
              for {
                response <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
              } yield response match {
                case Some(timetable) =>
                  // agar potochni bo`lsa
                  if (timetable.subjectId == timetableData.subjectId &&
                    timetable.teachers == timetableData.teachers &&
                    timetableData.flow == timetable.flow &&
                    timetableData.flow &&
                    timetableData.typeOfLesson == "Lecture" &&
                    timetableData.alternation != timetable.alternation
                  ) {
                    timetableDao.addTimetable(timetableData)
                    Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
                  }
                  else {
                    Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni " +
                      translateAlternation(timetable.alternation) + " haftada shu parada " +
                      timetable.teachers + " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                  }
                case None =>
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
            case "Laboratory" =>
              for {
                response <- timetableDao.findGroup(timetableData.weekDay, timetableData.couple,
                  timetableData.studyShift, timetableData.groups, timetableData.subjectId, timetableData.typeOfLesson)
                conflicts <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
                if conflicts.isEmpty
              } yield response match {
                case Some(timetable) =>
                  log.warning(s"megalka dars bor")
                  if (timetable.alternation != timetableData.alternation &&
                    timetable.teachers != timetableData.teachers &&
                    timetable.numberRoom != timetableData.numberRoom
                  ) {
                    for {
                      selectedTimetable <- timetableDao.getTimetableById(timetable.id)
                      updatedTimetable = selectedTimetable.get.copy(
                        teachers = Json.toJson(timetable.teachers,timetableData.teachers),
                        numberRoom = Json.toJson(timetable.numberRoom,timetableData.numberRoom)
                      )
                      update <- timetableDao.update(updatedTimetable)
                    } yield update
                    Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
                  } else {
                    Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni " +
                      translateAlternation(timetable.alternation) + " haftada shu parada " +
                      timetable.teachers + " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                  }
                case None =>
                  log.warning(s"megalka dars yo`q")
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
            case _ =>
              for {
                response <- timetableDao.findConflicts(timetableData.weekDay, timetableData.couple,
                  timetableData.numberRoom, timetableData.studyShift)
              } yield response match {
                case Some(timetable) =>
                  if (timetable.alternation != timetableData.alternation) {
                    timetableDao.addTimetable(timetableData)
                    Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
                  }
                  else {
                    Future.successful(Left(trWeekday(timetableData.weekDay) + " kuni " +
                      translateAlternation(timetable.alternation) + " haftada shu parada " +
                      timetable.teachers + " ismli o'qituvchini " + timetableData.numberRoom + " honada darsi bor!"))
                  }
                case None =>
                  timetableDao.addTimetable(timetableData)
                  Future.successful(Right(timetableData.teachers + "ismli o'qituvchi darsi dars jadvaliga qo'shildi"))
              }
          }

      }
      ).flatten
  }

  private def deleteTimetable(id: Int): Future[Int] = {
    timetableDao.delete(id)
    Future(id)
  }

  private def updateTimetable(timetable: Timetable): Future[Option[Int]] = {
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
      update <- timetableDao.update(updatedTimetable)
      response = selectedTimetable.get.id
    } yield {
      response
    }
  }


  private def getTimetableList: Future[Seq[Timetable]] = {
    timetableDao.getTimetables
  }

  private def teacherName(teacherName: JsValue): Future[Seq[Timetable]] = {
    timetableDao.getTimetablesByTeacher(teacherName)
  }

  private def GetEmptyRoomByCouple(presentCouple: GetEmptyRoom): Future[Seq[String]] = {
    for {
      presentLessons <- timetableDao.getBusyRoom(presentCouple.weekDay, presentCouple.couple, presentCouple.studyShift)
    } yield roomList.map(_.numberRoom).diff(presentLessons.map(_.numberRoom))
  }

  private def getTimetableByGroup(getText: GetText) = {
    updateCount(getText.group)
    for {
      response <- timetableDao.getTimetableByGroup(getText.weekDay, getText.group)
    } yield response.map { timetable =>
      val timetableMapped = timetable.copy(
        weekDay = trWeekday(timetable.weekDay),
        studyShift = trStudyShift(timetable.studyShift),
        couple = trCouple(timetable.couple),
        typeOfLesson = trTypeLesson(timetable.typeOfLesson)
      )
      timetableMapped
    }
  }

  private def getTimetableForTeacher(getText: GetTeacher) = {
    for {
      response <- timetableDao.getTByTeacherAndWeekday(getText.weekDay, getText.teacher)
    } yield response.map { timetable =>
      val timetableMapped = timetable.copy(
        weekDay = trWeekday(timetable.weekDay),
        studyShift = trStudyShift(timetable.studyShift),
        couple = trCouple(timetable.couple),
        typeOfLesson = trTypeLesson(timetable.typeOfLesson)
      )
      timetableMapped

    }
  }

  private def timetableForGroup(group: String) = {
    for {
      response <- timetableDao.getTimetableByGr(group)
    } yield response.map { timetable =>
      val timetableMapped = timetable.copy(
        studyShift = trStudyShift(timetable.studyShift),
        couple = trCouple(timetable.couple),
        typeOfLesson = trTypeLesson(timetable.typeOfLesson)
      )
      timetableMapped

    }
  }

  private def getTTeacher(teacher: JsValue): Future[Seq[Timetable]] = {
    for {
      response <- timetableDao.getTimetablesByTeacher(teacher)
    } yield response.map { timetable =>
      val timetableMapped = timetable.copy(
        weekDay = trWeekday(timetable.weekDay),
        studyShift = trStudyShift(timetable.studyShift),
        couple = trCouple(timetable.couple),
        typeOfLesson = trTypeLesson(timetable.typeOfLesson)
      )
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

  private def trWeekday(weekday: String) = {
    weekday match {
      case "Monday" => "Dushanba"
      case "Tuesday" => "Seshanba"
      case "Wednesday" => "Chorshanba"
      case "Thursday" => "Payshanba"
      case "Friday" => "Juma"
      case "Saturday" => "Shanba"
    }
  }

  private def translateAlternation(alternation: Option[String]) = {
    alternation match {
      case Some("even") => "juft"
      case Some("odd") => "toq"
    }
  }

  private def trStudyShift(studyShift: String) = {
    studyShift match {
      case "Afternoon" => "2 - Smena"
      case "Morning" => "1 - Smena"
    }
  }

  private def trTypeLesson(typeLesson: String) = {
    typeLesson match {
      case "Laboratory" => "Laboratoriya"
      case "Practice" => "Amaliyot"
      case "Lecture" => "Ma'ruza"
    }
  }

  private def trCouple(couple: String) = {
    couple match {
      case "couple 1" => "1 - Juftlik"
      case "couple 2" => "2 - Juftlik"
      case "couple 3" => "3 - Juftlik"
      case "couple 4" => "4 - Juftlik"
    }
  }
}
