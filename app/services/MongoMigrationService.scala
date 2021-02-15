package services

import configs.ConfigMongoDb
import play.api.inject.ApplicationLifecycle
import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.MongoCore3Driver
import com.github.cloudyrock.standalone.MongockStandalone
import com.mongodb.client.{MongoClient, MongoClients}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class MongoMigrationService @Inject()(implicit
                                      lifecycle: ApplicationLifecycle,
                                      config: ConfigMongoDb){

  import MongoMigrationService._

  val mongoClient: MongoClient = MongoClients.create(config.mongoDbUrl)
  val driver: MongoCore3Driver = MongoCore3Driver
    .withLockSetting(
      mongoClient,
      config.mongoDbName,
      config.migrationMaxDuration.toMinutes,
      config.migrationWaitingForLockTimeout.toMinutes,
      config.migrationMaxAcquireTries)
  driver.disableTransaction()
  driver.setChangeLogCollectionName(changeLogCollectionName)
  driver.setLockCollectionName(lockCollectionName)

  val runner: MongockStandalone.Runner = MongockStandalone.builder()
    .setDriver(driver)
    .addChangeLogsScanPackage(migrationPackage)
    .buildRunner()

  lifecycle.addStopHook { () => Future.successful(mongoClient.close()) }

  try {
    runner.execute()
  } finally {
    mongoClient.close()
  }
}

object MongoMigrationService {
  val migrationPackage = "mongo.migrations"
  val changeLogCollectionName = "mongockChangeLog"
  val lockCollectionName = "mongockLock"
}


