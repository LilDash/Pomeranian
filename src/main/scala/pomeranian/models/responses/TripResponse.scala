package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.trip.{ TripDetail, TripInfo, TripJsonProtocol, TripSummary }

case class GetTripDetailResponse(
  errCode: Int,
  errMsg: String,
  data: Option[TripDetail]) extends BaseResponse(errCode, errMsg)

case class GetTripsResponse(
  errCode: Int,
  errMsg: String,
  data: Seq[TripSummary]) extends BaseResponse(errCode, errMsg)

case class PublishTripResponse(
  errCode: Int,
  errMsg: String,
  data: Map[String, Int]) extends BaseResponse(errCode, errMsg)

case class GetMyTripsResponse(
  errCode: Int,
  errMsg: String,
  data: Seq[TripInfo]) extends BaseResponse(errCode, errMsg)

trait TripResponseJsonProtocol extends SprayJsonSupport with TripJsonProtocol {
  implicit val getTripDetailResponseFormat = jsonFormat3(GetTripDetailResponse)
  implicit val getTripsResponseFormat = jsonFormat3(GetTripsResponse)
  implicit val publishTripResponseFormat = jsonFormat3(PublishTripResponse)
  implicit val getMyTripsResponseFormat = jsonFormat3(GetMyTripsResponse)
}
