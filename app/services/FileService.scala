package services

import models.{EventKinesis, DtoEventKinesisAverage}

import java.io.File
import javax.inject.Inject
import scala.io.Source


class FileService @Inject()() {

  def readKinesisEventsFromFile(file: File, eventType: String, startDate: Long, endDate: Long): DtoEventKinesisAverage = {
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

    DtoEventKinesisAverage(eventType, avg.getOrElse(0.0), filteredEvents.size)
  }

}
