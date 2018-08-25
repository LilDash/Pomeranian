package pomeranian.models.video

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

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

//trait VideoReviewJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
//  implicit val videoReviewFormat = jsonFormat5(VideoReview)
//}