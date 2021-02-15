package services

import dao.DaoEventKinesis
import models.{DtoEventKinesisAverage, EventKinesis}

import java.io.File
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source


class FileService @Inject()()(implicit daoEventKinesis: DaoEventKinesis, ec : ExecutionContext) {


  def readKinesisEventsFromFile(file: File, eventType: String, startDate: Long, endDate: Long): Future[DtoEventKinesisAverage] = {
    val source: Source = Source.fromFile(file)
    val lines: List[String] = source.getLines().toList
    val filteredEvents: List[EventKinesis] = lines.flatMap(line => {
      line match {
        case s"$dateCreated,$name,$associatedValue"
          if eventType.equals(name) &&
            dateCreated.toLong > startDate &&
            dateCreated.toLong < endDate =>
          Some(EventKinesis(dateCreated.toLong, name, associatedValue.toDouble))
        case _ =>
          None
      }
    })
    val avg = if (filteredEvents.nonEmpty)
      Some(filteredEvents.map(_.associatedValue).sum / filteredEvents.size)
    else
      None
    val eventAverage = DtoEventKinesisAverage(eventType, avg.getOrElse(0.0), filteredEvents.size)

    // insert docs in mongo
    // this is fire and forget
    insertDoc(filteredEvents)

    Future.successful(eventAverage)
  }

  private def insertDoc(events: List[EventKinesis]): Future[Any] =
    try {
      daoEventKinesis.insertMany(events)
    } catch {
      case _: Exception =>
        // duplicate keys
        Future.successful(())
    }

}
