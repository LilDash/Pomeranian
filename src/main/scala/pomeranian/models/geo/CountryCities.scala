package pomeranian.models.geo

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol
import spray.json._

import scala.collection.mutable.ListBuffer

final case class CityBrief(
  cityId: Int,
  cityName: String,
  cityDisplayName: String)

final case class CountryCities(
  countryId: Int,
  countryName: String,
  countryDisplayName: String,
  cities: ListBuffer[CityBrief])

trait CountryCitiesJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val cityBriefFormat = jsonFormat3(CityBrief)
  implicit val countryCitiesFormat = jsonFormat4(CountryCities)
}