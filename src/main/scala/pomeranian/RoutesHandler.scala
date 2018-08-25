package pomeranian

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ RejectionHandler, Route }
import akka.util.Timeout
import ch.megard.akka.http.cors.javadsl.CorsRejection
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import pomeranian.routes.{ AuthorizationRoute, UploadRoute, VideoRoute }
import pomeranian.utils.AppConfiguration

import scala.concurrent.duration._

trait RoutesHandler extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[RoutesHandler])

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  // Load CORS setting
  val corsSetting = CorsSettings.fromSubConfig(AppConfiguration.corsConfig)
  implicit def rejectionHandler =
    RejectionHandler.newBuilder()
      .handleAll[CorsRejection] { _ =>
        complete(StatusCodes.Forbidden, s"Cross origin access is forbidden")
      }.result()

  //#all-routes
  lazy val authorizationRoute = new AuthorizationRoute()
  lazy val videoRoute = new VideoRoute()
  lazy val uploadRoute = new UploadRoute()

  // routes allow CORS
  val corsRoute = cors(corsSetting) {
    authorizationRoute.route ~ uploadRoute.route
  }

  lazy val routes: Route = {
    corsRoute // ~ nonCorsRoute
  }
}
