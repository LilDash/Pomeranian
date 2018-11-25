package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.trip.{TripInfo, TripJsonProtocal}

case class GetTripDetailResponse (
                                  errCode: Int,
                                  errMsg: String,
                                  version: String,
                                  data: Option[TripInfo]) extends BaseResponse(errCode, errMsg, version)


case class GetTripsResponse (
  errCode: Int,
  errMsg: String,
  version: String,
  data: Seq[TripInfo]) extends BaseResponse(errCode, errMsg, version)

trait TripResponseJsonProtocol extends SprayJsonSupport with TripJsonProtocal {
  implicit val getTripDetailResponseFormat = jsonFormat4(GetTripDetailResponse)
  implicit val getTripsResponseFormat = jsonFormat4(GetTripsResponse)
}
