package pomeranian.routes

import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.UploadPolicy
import pomeranian.models.responses.{ GetUploadPolicyResponse, UploadResponseJsonProtocol }
import pomeranian.services.OssServiceImpl

class UploadRoute extends UploadResponseJsonProtocol {

  val route: Route = {
    val version = "1"

    pathPrefix("upload") {
      path("policy") {
        pathEnd {
          get {
            val ossService = new OssServiceImpl()
            val futureResult = ossService.GetUploadPolicy()
            onSuccess(futureResult) { result: UploadPolicy =>
              val response = GetUploadPolicyResponse(ErrorCode.Ok, "", version, result)
              complete(StatusCodes.OK, response)
            }
          }
        }
      }
    }
  }
}
