package models

import play.api.libs.json.{Json, Writes}

object DtoEventKinesisAverage {
  implicit val jsWrites: Writes[DtoEventKinesisAverage] = new Writes[DtoEventKinesisAverage] {
    def writes(dto: DtoEventKinesisAverage) = Json.obj(
      "type" -> dto.name,
      "value" -> dto.avg,
      "processedCount" -> dto.processedCount
    )

  }
}

case class DtoEventKinesisAverage(name: String,
                                  avg: Double,
                                  processedCount: Int)
