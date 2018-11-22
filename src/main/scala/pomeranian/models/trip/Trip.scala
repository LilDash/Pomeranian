package pomeranian.models.trip

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class Trip (
  id: Int,
  userId: Int,
  departureCityId: Int,
  arrivalCityId: Int,
  flightNumber: String,
  totalCapacity: Int,
  remainingCapacity: Int,
  capacityPrice: Float,
  currency: String,
  departureTime: Timestamp,
  pickupTime: Timestamp,
  memo: Option[String],
  recStatus: Int,
  recCreatedWhen: Timestamp,
  recUpdatedWhen: Timestamp,
)

final case class TripInfo (
  id: Int,
  userId: Int,
  nickname: String,
  avatar: String,
  departureCityId: Int,
  departureCityName: String,
  departureCountryId: Int,
  departureCountryName: String,
  arrivalCityId: Int,
  arrivalCityName: String,
  arrivalCountryId: Int,
  arrivalCountryName: String,
  flightNumber: String,
  totalCapacity: Int,
  remainingCapacity: Int,
  capacityPrice: Float,
  currency: String,
  departureTime: Timestamp,
  pickupTime: Timestamp,
  memo: Option[String],
  recCreatedWhen: Timestamp,
)

trait TripJsonProtocal extends SprayJsonSupport with CommonJsonProtocol {
  implicit val tripInfoFormat = jsonFormat21(TripInfo)
}