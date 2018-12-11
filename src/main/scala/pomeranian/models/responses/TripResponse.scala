package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.trip.{TripDetail, TripJsonProtocol, TripSummary}

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

trait TripResponseJsonProtocol extends SprayJsonSupport with TripJsonProtocol {
  implicit val getTripDetailResponseFormat = jsonFormat4(GetTripDetailResponse)
  implicit val getTripsResponseFormat = jsonFormat4(GetTripsResponse)
}
