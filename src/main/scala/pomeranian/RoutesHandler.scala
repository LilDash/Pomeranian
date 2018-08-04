package pomeranian

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import pomeranian.routes.{ AuthorizationRoute, VideoRoute }
import scala.concurrent.duration._

trait RoutesHandler extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[RoutesHandler])

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  lazy val authorizationRoute = new AuthorizationRoute()
  lazy val videoRoute = new VideoRoute()

  lazy val routes: Route = authorizationRoute.route ~ videoRoute.route
  //#all-routes
}
