package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import pomeranian.constants.{AuthType, ErrorCode}
import pomeranian.models.login.MiniProgramLoginResultInfo
import pomeranian.models.requests.{LoginRequestJsonProtocol, WeChatMiniProgramLoginRequest}
import pomeranian.models.responses.{AuthLoginBaseResponse, AuthResponseJsonProtocol, AuthWeChatMiniLoginResponse}
import pomeranian.models.security.Role
import pomeranian.models.user.UserInfo
import pomeranian.services.{AuthServiceImpl, UserService, UserServiceImpl}

import scala.util.{Failure, Success}


class AuthRoute(implicit system: ActorSystem) extends BaseRoute with LoginRequestJsonProtocol with AuthResponseJsonProtocol {

  val version = "1"

  lazy val authService = new AuthServiceImpl
  lazy val userService = new UserServiceImpl

  val route: Route = {

    pathPrefix("auth") {
      pathPrefix("login") {
        path("base") {
          post {
            formFieldMap { params =>
              val userName = params.getOrElse("username", "")
              val password = params.getOrElse("password", "")
              // TODO: check credential
              // TODO: check role
              val userId = "123"
              val authToken = createToken(userId, Role.Basic)
              val response = AuthLoginBaseResponse(ErrorCode.Ok, "", version, authToken.token, authToken.expirationTime)
              complete(StatusCodes.OK, response)
            }
          }
        } ~ path("wechatmini") {
          post {
            entity(as[WeChatMiniProgramLoginRequest]) { requestData =>
              val futureResult = authService.loginWithWeChatMiniProgram(requestData.code, requestData.encryptedData, requestData.iv)
              onComplete(futureResult) {
                case Success(result) =>
                  val response = AuthWeChatMiniLoginResponse(ErrorCode.Ok, "", version, result)
                  complete(StatusCodes.OK, response)
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
}
