package pomeranian.routes

import akka.event.Logging
import akka.http.scaladsl.server.{ RequestContext, Route }
import pomeranian.models.security.Role
import pomeranian.utils.security.AuthorizationHandler

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BaseRoute extends AuthorizationHandler {
  def route: Route

  def hasPermission(role: Role) = (ctx: RequestContext) => {
    Future {
      isVerify(ctx.request, role)
    }
  }
}
