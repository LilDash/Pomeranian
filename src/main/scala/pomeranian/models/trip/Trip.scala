package pomeranian.models.trip

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.user._
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
                        contactTypeId: Int,
                        contactValue: String,
                        memo: Option[String],
                        recStatus: Int,
                        recCreatedWhen: Timestamp,
                        recUpdatedWhen: Timestamp,
)

final case class TripInfo (
                            id: Int,
                            userId: Int,
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
                            contactTypeId: Int,
                            contactValue: String,
                            memo: Option[String],
                            recCreatedWhen: Timestamp,
)

final case class TripSummary (
                             tripInfo: TripInfo,
                             userInfo: UserInfo,
                             )

final case class TripDetail (
                            tripInfo: TripInfo,
                            userInfo: UserInfo,
                            )

trait TripJsonProtocol extends SprayJsonSupport
  with UserJsonProtocol
  with UserContactJsonProtocol
  with CommonJsonProtocol {
  implicit val tripInfoFormat = jsonFormat21(TripInfo)
  implicit val tripSummaryFormat = jsonFormat2(TripSummary)
  implicit val tripDetailFormat = jsonFormat2(TripDetail)
}