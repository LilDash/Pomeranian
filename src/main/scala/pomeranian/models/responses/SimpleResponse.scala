package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class SimpleResponse(
  errCode: Int,
  errMsg: String,
  version: String) extends BaseResponse(errCode, errMsg, version)

trait SimpleResponseJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val simpleResponseFormat = jsonFormat3(SimpleResponse)
}
