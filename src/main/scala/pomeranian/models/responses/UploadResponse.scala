package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.{ OssJsonProtocol, UploadPolicy }

case class GetUploadPolicyResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  uploadPolicy: UploadPolicy) extends BaseResponse(errCode, errMsg, version)

trait UploadResponseJsonProtocol extends SprayJsonSupport with OssJsonProtocol {
  implicit val getUploadPolicyResponseFormat = jsonFormat4(GetUploadPolicyResponse)
}
