package pomeranian.routes

import java.security.InvalidParameterException
import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import pomeranian.constants.{ErrorCode, Global}
import pomeranian.models.requests.{PublishTripRequest, TripRequestJsonProtocol}
import pomeranian.models.responses._
import pomeranian.models.security.Role
import pomeranian.models.trip.Trip
import pomeranian.services.{TripServiceImpl, UserServiceImpl}
import pomeranian.utils.TimeUtil

import scala.concurrent.Future
import scala.util.{Failure, Success}


class TripRoute extends BaseRoute with TripRequestJsonProtocol with SimpleResponseJsonProtocol with TripResponseJsonProtocol {
  val route: Route = {
    val version = "1"
    val numPerPage = 10
    val tripService = new TripServiceImpl
    val userService = new UserServiceImpl

    //authorizeAsync(hasPermission(Role.Basic)) {
    pathPrefix("trip") {
      pathEnd {
        delete {
          parameters('userId.as[Int], 'tripId.as[Int]) { (userId, tripId) =>
            val futureResult = tripService.deleteTrip(userId, tripId)
            onComplete(futureResult) {
              case Success(result) if result > 0 =>
                val response = SimpleResponse(ErrorCode.Ok, "", version)
                complete(StatusCodes.OK, response)
              case Success(result) =>
                val response = SimpleResponse(ErrorCode.DeleteTripFailed, "Delete trip failed", version)
                complete(StatusCodes.OK, response)
              case Failure(f) =>
                // TODO: log
                println(f.getMessage)
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~ path("detail") {
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
      } ~ path("publish") {
        pathEnd {
          post {
            entity(as[PublishTripRequest]) { requestData =>
                val now = TimeUtil.timeStamp()
                val trip = Trip(
                  0,
                  requestData.userId,
                  requestData.depCityId,
                  requestData.arrCityId,
                  requestData.flightNo,
                  requestData.totalCapacity,
                  requestData.remainingCapacity,
                  requestData.capacityPrice,
                  "CNY", // TODO: by default
                  new Timestamp(requestData.depTime),
                  new Timestamp(requestData.arrTime),
                  requestData.contactTypeId,
                  requestData.contactValue,
                  Option(requestData.memo),
                  Global.DbRecActive,
                  now,
                  now,
                )
                try {

                  val futureResult = tripService.createTrip(trip)
                  onComplete(futureResult) {
                    case Success(result) =>
                      val response = if (result > 0) {
                        PublishTripResponse(ErrorCode.Ok, "", version, Map("tripId" -> result))
                      } else {
                        PublishTripResponse(ErrorCode.CreateTripFailed, "Create trip failed", version, Map("tripId" -> result))
                      }
                      complete(StatusCodes.OK, response)
                    case Failure(f) =>
                      // TODO: log
                      println(f.getMessage)
                      complete(StatusCodes.InternalServerError)
                  }


                } catch {
                  case ex: InvalidParameterException => {
                    // TODO: log
                    println(ex.getMessage)
                    complete(StatusCodes.BadRequest)
                  }
                  case ex: Throwable => {
                    // TODO: log
                    println(ex.getMessage)
                    complete(StatusCodes.InternalServerError)
                  }

                }


            }

          }
        }
      } ~ path("mytrips") {
        pathEnd {
          get {
            parameters('userId.as[Int], 'page.as[Int] ? 0) { (userId, page) =>
              val futureResult = tripService.getTripsByUserId(userId, Math.max(page, 0)*numPerPage, numPerPage)
              onComplete(futureResult) {
                case Success(result) =>
                  val response = GetMyTripsResponse(ErrorCode.Ok, "", version, result)
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
