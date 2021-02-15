package dao

import models.EventKinesis
import org.mongodb.scala.MongoDatabase

import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class DaoEventKinesis (implicit mongoDatabase: MongoDatabase, ec: ExecutionContext){

  def insertMany(event: Seq[EventKinesis]) = {
    mongoDatabase
      .getCollection("resident")
      .insertMany(event.map(_.toDocument))
      .toFuture()
  }
}
