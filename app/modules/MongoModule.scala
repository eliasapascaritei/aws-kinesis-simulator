package modules

import configs.ConfigMongoDb
import dao.DaoEventKinesis
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.inject._
import play.api.Configuration
import play.api.inject.SimpleModule
import services.{FileService, MongoMigrationService}

import scala.concurrent.ExecutionContext

class MongoModule extends SimpleModule(
  (_, conf) => {
    implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    implicit lazy val config: Configuration = conf
    implicit lazy val mongoConfig: ConfigMongoDb = new ConfigMongoDb()

    implicit lazy val mongoClient: MongoClient = {
      // enable ssl for mongo
//      System.setProperty("org.mongodb.async.type", "netty")
      MongoClient(mongoConfig.mongoDbUrl)
    }

    implicit lazy val mongoDb: MongoDatabase = mongoClient.getDatabase(mongoConfig.mongoDbName)

    implicit lazy val daoEventKinesis = new DaoEventKinesis()
    implicit lazy val fileService = new FileService()

    Seq(
      bind[MongoClient].toInstance(mongoClient),
      bind[MongoDatabase].toInstance(mongoDb),
      bind[MongoMigrationService].toSelf.eagerly(),
      bind[DaoEventKinesis].toInstance(daoEventKinesis),
      bind[FileService].toInstance(fileService)
    )
  }
)
