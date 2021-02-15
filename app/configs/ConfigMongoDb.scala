package configs

import play.api.Configuration

import javax.inject.Inject
import scala.concurrent.duration.Duration

class ConfigMongoDb @Inject()()(implicit val conf: Configuration) {
  val mongoDbUrl: String = conf.get[String]("mongodb.url")
  val mongoDbName: String = conf.get[String]("mongodb.db-name")
  val migrationMaxDuration: Duration = conf.get[Duration]("mongodb.migration-lock.max-duration")
  val migrationWaitingForLockTimeout: Duration = conf.get[Duration]("mongodb.migration-lock.waiting-for-lock-timeout")
  val migrationMaxAcquireTries: Int = conf.get[Int]("mongodb.migration-lock.max-acquire-tries")
}
