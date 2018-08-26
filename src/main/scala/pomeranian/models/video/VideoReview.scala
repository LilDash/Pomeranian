package pomeranian.models.video

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class VideoReview(
                              id: Long,
                              videoId: Long,
                              priority: Int,
                              remark: String,
                              reviewer: Long,
                              result: Int,
                              recActive: Int,
                              recCreateTime: Timestamp,
                              recUpdateTime: Timestamp)

object VideoReviewResult {
  val Pending = 0
  val Pass = 1
  val Blocked = 2
}

final case class VideoReviewDetail(
                                  id: Long,
                                  videoId: Long,
                                  priority: Int,
                                  remark: String,
                                  reviewer: Long,
                                  result: Int,
                                  videoKey: String,
                                  videoTitle: String,
                                  videoMimeType: String,
                                  videoSize: Long,
                                  videoMetadata: String,
                                  videoUploadTime: Timestamp,
                                  )

trait VideoReviewJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val videoReviewDetailFormat = jsonFormat12(VideoReviewDetail)
}