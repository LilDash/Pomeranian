package pomeranian.services

import pomeranian.constants.Global
import pomeranian.models.video.{VideoReview, VideoReviewDetail, VideoReviewResult}
import pomeranian.repositories.VideoReviewRepository
import pomeranian.utils.TimeUtil

import scala.concurrent.Future

trait VideoReviewService {
  def createVideoReview(videoId: Long): Future[Long]
  def getPendingVideoReview(offset: Long, num: Int): Future[Seq[Any]]
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

  override def getPendingVideoReview(offset: Long, num: Int): Future[Seq[VideoReviewDetail]] = {
    VideoReviewRepository.fetchPending(offset,num)
  }
}
