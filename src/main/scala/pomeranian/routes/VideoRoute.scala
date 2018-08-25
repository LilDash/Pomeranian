package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{UploadVideoResponse, VideoResponseJsonProtocol}
import pomeranian.models.video.VideoInfo
import pomeranian.services.VideoServiceImpl

class VideoRoute extends VideoResponseJsonProtocol {

//  val route: Route = {
//    val version = "1"
//
//    pathPrefix("video") {
//      pathEnd {
//        put {
//          fileUpload("file") {
//            case (fileInfo, fileStream) =>
//              val videoService = new VideoServiceImpl()
//              val futureResult = videoService.handleUpload(fileInfo, fileStream)
//
//              onSuccess(futureResult) { result: VideoInfo =>
//                val response = UploadVideoResponse(ErrorCode.Ok, "", version, result)
//                complete((StatusCodes.Created, response))
//              }
//          }
//        }
//      }
//    }
//  }
}
