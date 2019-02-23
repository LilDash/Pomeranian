package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.geo.{ CountryCities, CountryCitiesJsonProtocol }

case class GetCountryCitiesResponse(
  errCode: Int,
  errMsg: String,
  data: Seq[CountryCities]) extends BaseResponse(errCode, errMsg)

trait GeoResponseJsonProtocol extends SprayJsonSupport with CountryCitiesJsonProtocol {
  implicit val getCountryCitiesResponseFormat = jsonFormat3(GetCountryCitiesResponse)
}
