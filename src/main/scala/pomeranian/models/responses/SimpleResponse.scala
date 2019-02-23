package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class SimpleResponse(
  errCode: Int,
  errMsg: String) extends BaseResponse(errCode, errMsg)

trait SimpleResponseJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val simpleResponseFormat = jsonFormat2(SimpleResponse)
}
