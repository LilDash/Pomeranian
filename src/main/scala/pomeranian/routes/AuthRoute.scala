package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.PathDirectives.path
import pomeranian.http.Directives._
import pomeranian.models.requests.{LoginRequestJsonProtocol, WeChatMiniProgramLoginRequest}
import pomeranian.models.responses.AuthResponseJsonProtocol
import pomeranian.services.AuthService

class AuthRoute(authService: AuthService)(implicit system: ActorSystem)
  extends BaseRoute with LoginRequestJsonProtocol with AuthResponseJsonProtocol {

  val route: Route = {

    pathPrefix("auth") {
      pathPrefix("login") {
        path("wechatmini") {
          post {
            handle { request: WeChatMiniProgramLoginRequest => authService.loginWithWeChatMiniProgram(request) }
          }
        }
      }
    }

  }
}
