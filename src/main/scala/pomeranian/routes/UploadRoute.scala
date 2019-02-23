package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.{ OssUploadPolicy, UploadNotification }
import pomeranian.models.responses.{ GetUploadPolicyResponse, SaveUploadNotificationResponse, UploadResponseJsonProtocol }
import pomeranian.services.UploadServiceImpl

import scala.util.{ Failure, Success }

class UploadRoute extends BaseRoute with UploadResponseJsonProtocol {

  val route: Route = {

    pathPrefix("upload") {
      path("policy") {
        pathEnd {
          get {
            val uploadService = new UploadServiceImpl()
            val futureResult = uploadService.getOssUploadPolicy()
            onSuccess(futureResult) { result: OssUploadPolicy =>
              val response = GetUploadPolicyResponse(ErrorCode.Ok, "", result)
              complete(StatusCodes.OK, response)
            }
          }
        }
      } ~ path("callback") {
        pathEnd {
          post {
            complete(StatusCodes.OK, "")
          }
        }
      } ~ path("notification") {
        pathEnd {
          post {
            formFieldMap { params =>
              val storage = params.getOrElse("storage", "")
              val mimeType = params.getOrElse("mimeType", "")
              val key = params.getOrElse("key", "")
              val title = params.getOrElse("title", "")
              val size = params.getOrElse("size", "0").toInt
              val metadata = params.getOrElse("metadata", "")
              val notification = UploadNotification(storage, mimeType, key, title, size, metadata)
              val uploadService = new UploadServiceImpl()
              val futureResult = uploadService.saveUploadNotification(notification)
              onComplete(futureResult) {
                case Success(result: Long) if result > 0 =>
                  val response = SaveUploadNotificationResponse(ErrorCode.Ok, "", result)
                  complete(StatusCodes.OK, response)
                case Success(result: Long) if result <= 0 =>
                  val response = SaveUploadNotificationResponse(ErrorCode.SaveFailed, "Save notification failed", result)
                  complete(StatusCodes.OK, response)
                case Failure(e: IllegalArgumentException) =>
                  // TODO: log
                  complete(StatusCodes.BadRequest, e.getMessage)
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
