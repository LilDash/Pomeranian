package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{RequestContext, Route}
import pomeranian.models.security.Role
import pomeranian.utils.measurement.Measurer
import pomeranian.utils.security.AuthorizationHandler

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class BaseRoute(implicit system: ActorSystem, measurer: Measurer) extends AuthorizationHandler {
  def route: Route

  def hasPermission(role: Role) = (ctx: RequestContext) => {
    Future {
      isVerify(ctx.request, role)
    }
  }
}
