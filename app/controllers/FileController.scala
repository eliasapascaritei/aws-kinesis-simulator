package controllers


import play.api.libs.json.Json
import play.api.mvc._
import services.FileService

import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject._
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FileController @Inject()(implicit cc: ControllerComponents,
                               fileService: FileService) extends AbstractController(cc) {

  import models.DtoEventKinesisAverage._
  private val minuteInterval = 1

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
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
                  .map(temporaryFile => {
                    temporaryFile.ref.path.toFile
                  })
                val summary = fileService.readKinesisEventsFromFile(maybeSourceFile.get, name, startDate, endDate)

                Future.successful(Ok(Json.toJson(summary)))
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
      //        .recover(BadRequest("Something went wrong"))
    } catch {
      case _: Throwable =>
        Future.successful(BadRequest("Something went wrong"))
    }
}
