package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{ GetVideoReviewPendingResponse, VideoResponseJsonProtocol }
import pomeranian.models.security.Role
import pomeranian.services.VideoReviewServiceImpl

import scala.util.{ Failure, Success }

class VideoRoute extends BaseRoute with VideoResponseJsonProtocol {

  val route: Route = {
    val version = "1"
    val numPerPage = 10

    pathPrefix("video") {
      pathPrefix("review") {
        authorizeAsync(hasPermission(Role.Admin)) {
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
}
