package pomeranian.models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class PublishTripRequest (
                                depCityId: Int,
                                arrCityId: Int,
                                depTime: Long,
                                arrTime: Long,
                                flightNo: String,
                                capacityPrice: Int,
                                totalCapacity: Int,
                                remainingCapacity: Int,
                                memo: String
                               )

trait TripRequestJsonProtocol extends SprayJsonSupport with CommonJsonProtocol{
  implicit val publishTripRequestFormat = jsonFormat9(PublishTripRequest)
}
