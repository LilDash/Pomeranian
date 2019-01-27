package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.user.{UserContactInfo, UserContactJsonProtocol, UserInfo, UserJsonProtocol}
import pomeranian.utils.CommonJsonProtocol

//case class WeChatDecryptUserInfoResponse (
//                                   errCode: Int,
//                                   errMsg: String,
//                                   version: String,
//                                   data: Option[WeChatDecryptedUserInfo]) extends BaseResponse(errCode, errMsg, version)

case class GetUserInfoResponse (
                               errCode: Int,
                               errMsg: String,
                               version: String,
                               data: Option[UserInfo]
                               ) extends BaseResponse(errCode, errMsg, version)

case class GetUserContactsResponse (
                                 errCode: Int,
                                 errMsg: String,
                                 version: String,
                                 data: Seq[UserContactInfo]
                               ) extends BaseResponse(errCode, errMsg, version)

trait UserResponseJsonProtocol extends SprayJsonSupport with UserJsonProtocol with UserContactJsonProtocol with CommonJsonProtocol {
//  implicit val wechatDecryptUserInfoResponseFormat = jsonFormat4(WeChatDecryptUserInfoResponse)
  implicit val getUserInfoResponseFormat = jsonFormat4(GetUserInfoResponse)
  implicit val getUserContactsResponseFormat = jsonFormat4(GetUserContactsResponse)
}
