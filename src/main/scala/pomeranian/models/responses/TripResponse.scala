package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.trip.{TripDetail, TripInfo, TripJsonProtocol, TripSummary}

case class GetTripDetailResponse (
                                  errCode: Int,
                                  errMsg: String,
                                  version: String,
                                  data: Option[TripDetail]) extends BaseResponse(errCode, errMsg, version)


case class GetTripsResponse (
  errCode: Int,
  errMsg: String,
  version: String,
  data: Seq[TripSummary]) extends BaseResponse(errCode, errMsg, version)

case class PublishTripResponse (
                               errCode: Int,
                               errMsg: String,
                               version: String,
                               data: Map[String, Int]
                               ) extends BaseResponse(errCode, errMsg, version)

case class GetMyTripsResponse (
                              errCode: Int,
                              errMsg: String,
                              version: String,
                              data: Seq[TripInfo]
                              ) extends BaseResponse(errCode, errMsg, version)

trait TripResponseJsonProtocol extends SprayJsonSupport with TripJsonProtocol {
  implicit val getTripDetailResponseFormat = jsonFormat4(GetTripDetailResponse)
  implicit val getTripsResponseFormat = jsonFormat4(GetTripsResponse)
  implicit val publishTripResponseFormat = jsonFormat4(PublishTripResponse)
  implicit val getMyTripsResponseFormat = jsonFormat4(GetMyTripsResponse)
}
