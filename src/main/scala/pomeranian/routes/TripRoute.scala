package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.http.Directives._
import pomeranian.models.requests.{ PublishTripRequest, TripRequestJsonProtocol }
import pomeranian.models.responses._
import pomeranian.models.security.Role
import pomeranian.services.TripService
import pomeranian.utils.measurement.Measurer

class TripRoute(tripService: TripService)(implicit system: ActorSystem, measurer: Measurer)
  extends BaseRoute with TripRequestJsonProtocol with SimpleResponseJsonProtocol with TripResponseJsonProtocol {
  val route: Route = {
    val numPerPage = 10

    authorizeAsync(hasPermission(Role.Basic)) {
      pathPrefix("trip") {
        pathEnd {
          delete {
            parameters('userId.as[Int], 'tripId.as[Int]) { (userId, tripId) =>
              complete(tripService.deleteTrip(userId, tripId))
            }
          }
        } ~ path("detail") {
          pathEnd {
            get {
              parameters('id.as[Int]) { (tripId) =>
                complete(tripService.getTripDetailById(tripId))
              }
            }
          }
        } ~ path("search") {
          pathEnd {
            get {
              parameters(
                'page.as[Int] ? 0,
                'depCountryId.as[Int] ? 0,
                'depCityId.as[Int] ? 0,
                'arrCountryId.as[Int] ? 0,
                'arrCityId.as[Int] ? 0) { (page, depCountryId, depCityId, arrCountryId, arrCityId) =>
                  complete(tripService.searchTripsByLocation(
                    depCountryId,
                    depCityId,
                    arrCountryId,
                    arrCityId,
                    Math.max(page, 0) * numPerPage, numPerPage))
                }
            }
          }
        } ~ path("publish") {
          pathEnd {
            post {
              handle { request: PublishTripRequest => tripService.createTrip(request) }
            }
          }
        } ~ path("mytrips") {
          pathEnd {
            get {
              parameters('userId.as[Int], 'page.as[Int] ? 0) { (userId, page) =>
                complete(tripService.getTripsByUserId(userId, Math.max(page, 0) * numPerPage, numPerPage))
              }
            }
          }
        }
      }
    }
  }
}
