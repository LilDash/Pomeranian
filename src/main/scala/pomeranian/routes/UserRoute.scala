package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.{ ErrorCode, Global }
import pomeranian.models.requests.{ SaveContactsRequest, UserRequestJsonProtocol }
import pomeranian.models.responses._
import pomeranian.models.security.Role
import pomeranian.services.{ UserServiceImpl, WeChatServiceImpl }
import pomeranian.utils.measurement.Measurer

import scala.util.{ Failure, Success }

class UserRoute(implicit system: ActorSystem, measurer: Measurer) extends BaseRoute
  with UserRequestJsonProtocol
  with SimpleResponseJsonProtocol
  with UserResponseJsonProtocol {

  val route: Route = {
    lazy val userService = new UserServiceImpl

    //authorizeAsync(hasPermission(Role.Basic)) {
    pathPrefix("user") {
      path("info") {
        get {
          parameters('id.as[Int]) { (userId) =>
            val futureResult = userService.findUserByUserId(userId)
            onComplete(futureResult) {
              case Success(result) =>
                if (result.isDefined) {
                  val response = GetUserInfoResponse(ErrorCode.Ok, "", result)
                  complete(StatusCodes.OK, response)
                } else {
                  val response = GetUserInfoResponse(
                    ErrorCode.UserNotFound, s"User id: ${userId} not found ", None)
                  complete(StatusCodes.NotFound, response)
                }

              case Failure(f) =>
                // TODO: log

                println(f.getMessage)
                complete(StatusCodes.InternalServerError)
            }
          }
        }
        //      } ~ path("contacts") {
        //        get {
        //          parameters('userId.as[Int]) { (userId) =>
        //            val futureResult = userService.getContacts(userId)
        //            onComplete(futureResult) {
        //              case Success(result) =>
        //                val response = GetUserContactsResponse(ErrorCode.Ok, "", version, result)
        //                complete(StatusCodes.OK, response)
        //              case Failure(f) =>
        //                // TODO: log
        //                println(f.getMessage)
        //                complete(StatusCodes.InternalServerError)
        //            }
        //          }
        //        } ~ post {
        //          entity(as[SaveContactsRequest]) { requestData =>
        //            val futureResult = userService.saveContacts(requestData.userId, requestData.contacts)
        //            onComplete(futureResult) {
        //              case Success(true) =>
        //                complete(StatusCodes.OK, SimpleResponse(ErrorCode.Ok, "", version))
        //              case Success(false) =>
        //                complete(StatusCodes.OK, SimpleResponse(ErrorCode.SaveFailed, "Save contacts failed", version))
        //              case Failure(f) =>
        //                // TODO: log
        //                println(f.getMessage)
        //                complete(StatusCodes.InternalServerError)
        //            }
        //          }
        //        }
      }

    }
    // }
  }
}
