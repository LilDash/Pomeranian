package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.{ErrorCode, Global}
import pomeranian.models.requests.{UserRequestJsonProtocol, WeChatDecryptUserInfoRequest}
import pomeranian.models.responses._
import pomeranian.models.security.Role
import pomeranian.services.WeChatServiceImpl

import scala.util.{Failure, Success}



class UserRoute(implicit system: ActorSystem) extends BaseRoute with UserRequestJsonProtocol with UserResponseJsonProtocol {
  val route: Route = {
    val version = "1"

    val wechatService = new WeChatServiceImpl;

    //authorizeAsync(hasPermission(Role.Basic)) {
    pathPrefix("user") {
      pathPrefix("wechat") {
        path("decryptUserInfo") {
          pathEnd {
            post {
              entity(as[WeChatDecryptUserInfoRequest]) { requestData =>
                val futureResult = wechatService.decryptUserInfo(requestData.code, requestData.encryptedData, requestData.iv)
                onComplete(futureResult) {
                  case Success(result) =>
                    val res = if(result.isDefined) {
                      WeChatDecryptUserInfoResponse(ErrorCode.Ok, "", version, result)
                    } else {
                      WeChatDecryptUserInfoResponse(ErrorCode.WeChatDecryptedUserInfoFailed, "", version, None)
                    }
                    complete(StatusCodes.OK, res)
                  case Failure(f) =>
                    // TODO: log
                    println(f.getMessage)
                    complete(StatusCodes.InternalServerError)
                }

              }
            }
          }
        }

      }


    }
    // }
  }
}
