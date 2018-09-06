package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.video.{ VideoInfo, VideoJsonProtocol, VideoReviewDetail, VideoReviewJsonProtocol }

case class UploadVideoResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  video: VideoInfo) extends BaseResponse(errCode, errMsg, version)

case class GetVideoReviewPendingResponse(
  errCode: Int,
  errMsg: String,
  version: String,
  videoReviews: Seq[VideoReviewDetail]) extends BaseResponse(errCode, errMsg, version)

trait VideoResponseJsonProtocol extends SprayJsonSupport with VideoJsonProtocol with VideoReviewJsonProtocol {
  implicit val uploadVideoResponseFormat = jsonFormat4(UploadVideoResponse)
  implicit val getVideoReviewPendingResponseFormat = jsonFormat4(GetVideoReviewPendingResponse)
}
