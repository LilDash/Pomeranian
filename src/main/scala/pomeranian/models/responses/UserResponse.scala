package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.user.{ UserContactInfo, UserContactJsonProtocol, UserInfo, UserJsonProtocol }
import pomeranian.utils.CommonJsonProtocol

case class GetUserInfoResponse(
  errCode: Int,
  errMsg: String,
  data: Option[UserInfo]) extends BaseResponse(errCode, errMsg)

case class GetUserContactsResponse(
  errCode: Int,
  errMsg: String,
  data: Seq[UserContactInfo]) extends BaseResponse(errCode, errMsg)

trait UserResponseJsonProtocol extends SprayJsonSupport with UserJsonProtocol with UserContactJsonProtocol with CommonJsonProtocol {
  implicit val getUserInfoResponseFormat = jsonFormat3(GetUserInfoResponse)
  implicit val getUserContactsResponseFormat = jsonFormat3(GetUserContactsResponse)
}
