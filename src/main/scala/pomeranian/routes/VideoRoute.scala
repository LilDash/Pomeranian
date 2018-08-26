package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{GetVideoReviewPendingResponse, UploadVideoResponse, VideoResponseJsonProtocol}
import pomeranian.models.video.VideoInfo
import pomeranian.services.{VideoReviewServiceImpl, VideoServiceImpl}

import scala.util.{Failure, Success}

class VideoRoute extends VideoResponseJsonProtocol {

  val route: Route = {
    val version = "1"
    val numPerPage = 10

    pathPrefix("video") {
        pathPrefix("review") {
          path("pending") {
            pathEnd {
              get {
                parameters('page.as[Int] ? 0) { (page) =>
                  val offset = page * numPerPage
                  val videoReviewService = new VideoReviewServiceImpl()
                  val futureResult = videoReviewService.getPendingVideoReview(offset, numPerPage)
                  onComplete(futureResult) {
                    case Success(result) =>
                      val response = GetVideoReviewPendingResponse(ErrorCode.Ok, "", version, result)
                      complete(StatusCodes.OK, response)
                    case Failure(_) =>
                      // TODO: log
                      complete(StatusCodes.InternalServerError)
                  }
                }
              }
            }
          }
        }
    }
  }
}
