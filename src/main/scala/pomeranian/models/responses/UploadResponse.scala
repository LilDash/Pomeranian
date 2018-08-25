package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.{ OssJsonProtocol, OssUploadPolicy }

case class GetUploadPolicyResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  uploadPolicy: OssUploadPolicy) extends BaseResponse(errCode, errMsg, version)

case class SaveUploadNotificationResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  id: Long) extends BaseResponse(errCode, errMsg, version)

trait UploadResponseJsonProtocol extends SprayJsonSupport with OssJsonProtocol {
  implicit val getUploadPolicyResponseFormat = jsonFormat4(GetUploadPolicyResponse)
  implicit val saveUPloadNotificationResponseFormat = jsonFormat4(SaveUploadNotificationResponse)
}
