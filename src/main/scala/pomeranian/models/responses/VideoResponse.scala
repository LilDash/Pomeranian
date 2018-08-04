package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.{ VideoInfo, VideoJsonProtocol }

case class UploadVideoResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  video: VideoInfo) extends BaseResponse(errCode, errMsg, version)

trait VideoResponseJsonProtocol extends SprayJsonSupport with VideoJsonProtocol {
  implicit val uploadVideoResponseFormat = jsonFormat4(UploadVideoResponse)
}
