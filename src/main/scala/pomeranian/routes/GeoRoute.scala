package pomeranian.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.ErrorCode
import pomeranian.models.responses.{GeoResponseJsonProtocol, GetCountryCitiesResponse}
import pomeranian.models.security.Role
import pomeranian.services.LocationServiceImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure


class GeoRoute extends BaseRoute with GeoResponseJsonProtocol {
  val route: Route = {
    val version = "1"
    val numPerPage = 10

    pathPrefix("geo") {
        //authorizeAsync(hasPermission(Role.Basic)) {
        path("countrycity") {
          pathEnd {
            get {
              val locationService = new LocationServiceImpl
              val futureResult = locationService.getCountryCityCollection
              onComplete(futureResult) {
                case Success(result) =>
                  val response = GetCountryCitiesResponse(ErrorCode.Ok, "", version, result)
                  complete(StatusCodes.OK, response)
                case Failure(_) =>
                  // TODO : Log
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
        // }
      }
    }
}
