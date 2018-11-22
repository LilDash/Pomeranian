package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.trip.{TripInfo, TripJsonProtocal}

case class GetTripsResponse (
  errCode: Int,
  errMsg: String,
  version: String,
  data: Seq[TripInfo]) extends BaseResponse(errCode, errMsg, version)

trait TripResponseJsonProtocol extends SprayJsonSupport with TripJsonProtocal {
  implicit val getTripsResponseFormat = jsonFormat4(GetTripsResponse)
}
