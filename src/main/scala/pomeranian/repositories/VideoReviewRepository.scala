package pomeranian.repositories

import pomeranian.models.video._
import pomeranian.utils.TimeUtil
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.{ Rep, TableQuery }
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait VideoReviewRepository {

  /**
   *
   * @param videoReview
   * @return id which is larger than 0 if inserted, 0 if failed.
   */
  def insert(videoReview: VideoReview): Future[Long]

  def fetchPending(offset: Long, num: Int): Future[Seq[VideoReviewDetail]]
}

object VideoReviewRepository extends VideoReviewRepository {
  val db = MySqlDbConnection.db

  val videoReviews = TableQuery[VideoReviewTableDef]
  val videos = TableQuery[VideoTableDef]

  override def insert(videoReview: VideoReview): Future[Long] = {
    val action = (videoReviews returning videoReviews.map(_.id)) += videoReview
    db.run(action).map(videoReviewId =>
      videoReviewId).recover {
      case ex: Exception =>
        //Logger.error(ex.getCause.getMessage())
        0
    }
  }

  override def fetchPending(offset: Long, num: Int): Future[Seq[VideoReviewDetail]] = {
    val query = videoReviews
      .joinLeft(videos)
      .on(_.videoId === _.id)
      .filter(_._1.result === VideoReviewResult.Pending)
      .sortBy { q => (q._1.priority.desc, q._1.id.desc) }
      .drop(offset).take(num)
      .result.map { rows =>
        rows.collect {
          case (review, Some(video)) =>
            VideoReviewDetail(
              review.id, review.videoId, review.priority, review.remark,
              review.reviewer, review.result, video.key, video.title, video.mimeType,
              video.size, video.metadata, video.recCreateTime)
          case (review, None) =>
            VideoReviewDetail(
              review.id, review.videoId, review.priority, review.remark,
              review.reviewer, review.result, "", "", "",
              0, "", TimeUtil.timeStamp())
        }
      }
    db.run(query)
  }
}
