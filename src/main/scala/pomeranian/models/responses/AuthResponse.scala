package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.login.{LoginResultJsonProtocol, MiniProgramLoginResult}
import pomeranian.utils.CommonJsonProtocol

case class AuthLoginBaseResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  token: String,
  expirationTime: Long,
) extends BaseResponse(errCode, errMsg, version)

case class AuthWeChatMiniLoginResponse(
                                      errCode: Int,
                                      errMsg: String,
                                      version: String,
                                      data: MiniProgramLoginResult,
                                      ) extends BaseResponse(errCode, errMsg, version)

trait AuthResponseJsonProtocol extends SprayJsonSupport with LoginResultJsonProtocol with CommonJsonProtocol {
  implicit val authLoginBaseResponseFormat = jsonFormat5(AuthLoginBaseResponse)
  implicit val authWeChatMiniLoginResponseFormat = jsonFormat4(AuthWeChatMiniLoginResponse)
}

