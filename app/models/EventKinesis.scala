package models

import org.mongodb.scala.bson.{BsonDateTime, BsonDocument, Document}

case class EventKinesis(dateCreated: Long,
                        name: String,
                        associatedValue: Double){

  def toDocument: Document = {
    val bsonDoc = BsonDocument(
      "dateCreated" -> BsonDateTime(dateCreated),
      "name" -> name,
      "associatedValue" -> associatedValue
    )

    new Document(bsonDoc)
  }
}
