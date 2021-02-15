package mongo.migrations

import com.github.cloudyrock.mongock.{ChangeLog, ChangeSet}
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters._
import com.mongodb.client.model.Indexes._
import com.mongodb.client.model._
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.mongodb.scala.model.{CreateCollectionOptions, IndexOptions}

@ChangeLog(order = "001")
class Migration001_SetupDb {

  @ChangeSet(order = "001", id = "20200215-001", author = "elias")
  def setupCollection(db: MongoDatabase): Unit = {

    db.createCollection("resident", new CreateCollectionOptions()
      .validationOptions(new ValidationOptions()
        .validator(
          jsonSchema(
            BsonDocument(Seq(
              "bsonType" -> BsonString("object"),
              "required" -> BsonArray.fromIterable(
                Array(
                  "dateCreated",
                  "name",
                  "associatedValue"
                ).map(BsonString(_))
              ),
              "properties" -> BsonDocument(Seq(
                "dateCreated" -> BsonDocument(Seq(
                  "bsonType" -> BsonString("date"),
                  "description" -> BsonString("Date obtained from kinesis/file")
                )),
                "name" -> BsonDocument(Seq(
                  "bsonType" -> BsonString("string"),
                  "description" -> BsonString("name of event")
                )),
                "associatedValue" -> BsonDocument(Seq(
                  "bsonType" -> BsonString("double"),
                  "description" -> BsonString("associated value")
                ))
              ))
            ))
          )
        )
        .validationAction(ValidationAction.ERROR)
      )
      .collation(
        Collation.builder()
          .locale("en")
          .collationStrength(CollationStrength.SECONDARY)
          .caseLevel(false)
          .numericOrdering(true)
          .build())
    )
  }

  @ChangeSet(order = "002", id = "20200215-002", author = "elias")
  def addIndex(db: MongoDatabase) = {
    val collection = db.getCollection("resident")
    // This index wil be used to shard the collection
    collection.createIndex(ascending(Seq(
      "dateCreated",
      "name",
      "_id"
    ): _*),
      IndexOptions().name("residentIndex")
    )
  }

  @ChangeSet(order = "003", id = "20200215-003", author = "elias")
  def addIndexUnique(db: MongoDatabase) = {
    val collection = db.getCollection("resident")
    // This index wil be used to shard the collection
    collection.createIndex(
      compoundIndex(
        ascending("dateCreated"),
        ascending("name"),
        ascending("associatedValue")
      ),
      IndexOptions().name("dateNameIndex").unique(true)
    )
  }

}
