package pomeranian.services

import pomeranian.constants.Global
import pomeranian.models.video.{VideoReview, VideoReviewResult}
import pomeranian.repositories.VideoReviewRepository
import pomeranian.utils.TimeUtil

import scala.concurrent.Future

trait VideoReviewService {
  def createVideoReview(videoId: Long): Future[Long]
}

class VideoReviewServiceImpl extends VideoReviewService {
  private val defaultPriority = 100

  override def createVideoReview(videoId: Long): Future[Long] = {
    val timeStamp = TimeUtil.timeStamp()
    val videoReview = VideoReview(0, videoId, defaultPriority, "",
                                  0, VideoReviewResult.Pending, Global.DbRecActive,
                                  timeStamp, timeStamp)
    VideoReviewRepository.insert(videoReview)
  }
}
