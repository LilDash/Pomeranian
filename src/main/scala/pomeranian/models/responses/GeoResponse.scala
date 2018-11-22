package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.geo.{CountryCities, CountryCitiesJsonProtocol}

case class GetCountryCitiesResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  data: Seq[CountryCities]) extends BaseResponse(errCode, errMsg, version)

trait GeoResponseJsonProtocol extends SprayJsonSupport with CountryCitiesJsonProtocol {
  implicit val getCountryCitiesResponseFormat = jsonFormat4(GetCountryCitiesResponse)
}
