package controllers


import play.api.libs.json.Json
import play.api.mvc._
import services.FileService

import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FileController @Inject()(implicit cc: ControllerComponents,
                               ec: ExecutionContext,
                               fileService: FileService) extends AbstractController(cc) {

  import models.DtoEventKinesisAverage._
  private lazy val minuteInterval = 1

  def index: Action[AnyContent] = Action {
    Ok("Resident.")
  }


  def uploadFile(name: String): Action[AnyContent] =
    Action.async { implicit request =>
      wrapException {
        val startDate = request.getQueryString("from").get.toLong
        val endDate = request.getQueryString("to").get.toLong

        isTimeIntervalCorrect(startDate, endDate) match {
          case false =>
            Future.successful(BadRequest("bad time interval"))
          case true =>
            request.body.asMultipartFormData match {
              case None =>
                Future.successful(BadRequest("File not present"))
              case Some(multiPartFormData) =>
                val maybeSourceFile: Option[File] = multiPartFormData
                  .file("file")
                  .map(_.ref.path.toFile)

                fileService
                  .readKinesisEventsFromFile(maybeSourceFile.get, name, startDate, endDate)
                  .map(result => {
                    Ok(Json.toJson(result))
                  })
            }
        }
      }
    }

  private def isTimeIntervalCorrect(startDate: Long, endDate: Long): Boolean = {
    val timeDiff = ChronoUnit.MINUTES.between(Instant.ofEpochSecond(startDate), Instant.ofEpochSecond(endDate))
    timeDiff > minuteInterval
  }

  private def wrapException(future: => Future[Result]): Future[Result] =
    try {
      future
    } catch {
      case _: Throwable =>
        Future.successful(BadRequest("Something went wrong"))
    }
}
