package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.{ OssJsonProtocol, OssUploadPolicy }

case class GetUploadPolicyResponse(
  errCode: Int,
  errMsg: String,
  uploadPolicy: OssUploadPolicy) extends BaseResponse(errCode, errMsg)

case class SaveUploadNotificationResponse(
  errCode: Int,
  errMsg: String,
  id: Long) extends BaseResponse(errCode, errMsg)

trait UploadResponseJsonProtocol extends SprayJsonSupport with OssJsonProtocol {
  implicit val getUploadPolicyResponseFormat = jsonFormat3(GetUploadPolicyResponse)
  implicit val saveUPloadNotificationResponseFormat = jsonFormat3(SaveUploadNotificationResponse)
}
