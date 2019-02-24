package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import org.slf4j.LoggerFactory
import pomeranian.models.responses.GeoResponseJsonProtocol
import pomeranian.services.LocationService
import pomeranian.utils.measurement.Measurer

class GeoRoute(locationService: LocationService)(implicit system: ActorSystem, measurer: Measurer)
  extends BaseRoute with GeoResponseJsonProtocol {

  lazy val logger = LoggerFactory.getLogger(this.getClass)

  val route: Route = {
    pathPrefix("geo") {
      path("countrycity") {
        pathEnd {
          get {
            complete(locationService.getCountryCityCollection)
          }
        }
      }
    }
  }
}
