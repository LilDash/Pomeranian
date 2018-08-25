package pomeranian.models.video

import java.sql.Timestamp
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class VideoReviewTableDef(tag: Tag) extends Table[VideoReview](tag, "video_review") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def videoId = column[Long]("video_id")
  def priority = column[Int]("priority")
  def remark = column[String]("remark")
  def reviewer = column[Long]("reviewer")
  def result = column[Int]("result")
  def recActive = column[Int]("rec_active")
  def recCreateTime = column[Timestamp]("rec_createtime")
  def recUpdateTime = column[Timestamp]("rec_updatetime")

  override def * = (
    id,
    videoId,
    priority,
    remark,
    reviewer,
    result,
    recActive,
    recCreateTime,
    recUpdateTime) <> (VideoReview.tupled, VideoReview.unapply)
}