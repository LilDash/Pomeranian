package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{GetTripDetailResponse, GetTripsResponse, TripResponseJsonProtocol}
import pomeranian.models.security.Role
import pomeranian.services.TripServiceImpl

import scala.util.{Failure, Success}


class TripRoute extends BaseRoute with TripResponseJsonProtocol {
  val route: Route = {
    val version = "1"
    val numPerPage = 10
    val tripService = new TripServiceImpl

    //authorizeAsync(hasPermission(Role.Basic)) {
    pathPrefix("trip") {
      path("detail") {
        pathEnd {
          get {
            parameters('id.as[Int]) { (tripId) =>
              val futureResult = tripService.getTripDetailById(tripId)
              onComplete(futureResult) {
                case Success(result) =>
                  if (result.isDefined) {
                    val response = GetTripDetailResponse(ErrorCode.Ok, "", version, result)
                    complete(StatusCodes.OK, response)
                  } else {
                    val response = GetTripDetailResponse(
                      ErrorCode.TripNotFound, s"Trip id: ${tripId} not found ", version, result)
                    complete(StatusCodes.NotFound, response)
                  }

                case Failure(f) =>
                  // TODO: log

                  println(f.getMessage)
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~ path("search") {
        pathEnd {
          get {
            parameters('page.as[Int] ? 0,
              'depCountryId.as[Int] ? 0,
              'depCityId.as[Int] ? 0,
              'arrCountryId.as[Int] ? 0,
              'arrCityId.as[Int] ? 0) { (page, depCountryId, depCityId, arrCountryId, arrCityId) =>
              val futureResult = tripService.searchTripsByLocation(
                depCountryId,
                depCityId,
                arrCountryId,
                arrCityId,
                Math.max(page, 0)*numPerPage, numPerPage)
              onComplete(futureResult) {
                case Success(result) =>
                  val response = GetTripsResponse(ErrorCode.Ok, "", version, result)
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
    // }
  }
}
