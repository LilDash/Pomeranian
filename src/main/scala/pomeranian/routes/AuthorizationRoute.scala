package pomeranian.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import pomeranian.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout

class AuthorizationRoute {

  val route: Route = {

    pathPrefix("auth") {
      pathPrefix("login") {
        path("base") {
          post {
            println("path: auth/login/base")
            complete("ok")
          }
        }
      } ~ pathPrefix("register") {
        path("base") {
          post {
            println("path: auth/register/base")
            complete("ok")
          }
        }
      }
    }

  }
}
