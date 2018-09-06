package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class AuthLoginBaseResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  token: String,
  expirationTime: Long,
)

trait AuthResponseJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val authLoginBaseResponseFormat = jsonFormat5(AuthLoginBaseResponse)
}

