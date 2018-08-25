package pomeranian.repositories

import pomeranian.models.video.{VideoReview, VideoReviewTableDef}
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
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
}

object VideoReviewRepository extends VideoReviewRepository {
  val db = MySqlDbConnection.db

  val videoReviews = TableQuery[VideoReviewTableDef]

  override def insert(videoReview: VideoReview): Future[Long] = {
    val action = (videoReviews returning videoReviews.map(_.id)) += videoReview
    db.run(action).map(videoReviewId =>
      videoReviewId
    ).recover {
      case ex: Exception =>
        //Logger.error(ex.getCause.getMessage())
        0
    }
  }
}
