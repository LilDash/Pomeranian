package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.login.{LoginResultJsonProtocol, MiniProgramLoginResult}
import pomeranian.utils.CommonJsonProtocol

case class AuthLoginBaseResponse(
  errCode: Int,
  errMsg: String,
  token: String,
  expirationTime: Long,
) extends BaseResponse(errCode, errMsg)

case class AuthWeChatMiniLoginResponse(
                                      errCode: Int,
                                      errMsg: String,
                                      data: MiniProgramLoginResult,
                                      ) extends BaseResponse(errCode, errMsg)

trait AuthResponseJsonProtocol extends SprayJsonSupport with LoginResultJsonProtocol with CommonJsonProtocol {
  implicit val authLoginBaseResponseFormat = jsonFormat4(AuthLoginBaseResponse)
  implicit val authWeChatMiniLoginResponseFormat = jsonFormat3(AuthWeChatMiniLoginResponse)
}

