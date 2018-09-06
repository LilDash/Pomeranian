package pomeranian.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{AuthLoginBaseResponse, AuthResponseJsonProtocol}
import pomeranian.models.security.Role

class AuthRoute extends BaseRoute with AuthResponseJsonProtocol {

  val version = "1"

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
        }
      }
    }

  }
}
