package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.wechat.{WeChatDecryptedUserInfo, WeChatDecryptedUserInfoJsonProtocol}
import pomeranian.utils.CommonJsonProtocol

case class WeChatDecryptUserInfoResponse (
                                   errCode: Int,
                                   errMsg: String,
                                   version: String,
                                   data: Option[WeChatDecryptedUserInfo]) extends BaseResponse(errCode, errMsg, version)

trait UserResponseJsonProtocol extends SprayJsonSupport with WeChatDecryptedUserInfoJsonProtocol with CommonJsonProtocol {
  implicit val wechatDecryptUserInfoResponseFormat = jsonFormat4(WeChatDecryptUserInfoResponse)
}
