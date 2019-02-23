package pomeranian.models.responses

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.video.{ VideoInfo, VideoJsonProtocol, VideoReviewDetail, VideoReviewJsonProtocol }

case class UploadVideoResponse(
  errCode: Int,
  errMsg: String,
  video: VideoInfo) extends BaseResponse(errCode, errMsg)

case class GetVideoReviewPendingResponse(
  errCode: Int,
  errMsg: String,
  videoReviews: Seq[VideoReviewDetail]) extends BaseResponse(errCode, errMsg)

trait VideoResponseJsonProtocol extends SprayJsonSupport with VideoJsonProtocol with VideoReviewJsonProtocol {
  implicit val uploadVideoResponseFormat = jsonFormat3(UploadVideoResponse)
  implicit val getVideoReviewPendingResponseFormat = jsonFormat3(GetVideoReviewPendingResponse)
}
