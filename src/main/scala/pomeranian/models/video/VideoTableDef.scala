package pomeranian.models.video

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class VideoTableDef(tag: Tag) extends Table[Video](tag, "video") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def key = column[String]("key", O.Unique)
  def storage = column[String]("storage")
  def title = column[String]("title")
  def mimeType = column[String]("mime_type")
  def size = column[Long]("size")
  def metadata = column[String]("metadata")
  def recActive = column[Int]("rec_active")
  def recCreateTime = column[Timestamp]("rec_createtime")
  def recUpdateTime = column[Timestamp]("rec_updatetime")

  override def * = (
    id,
    key,
    storage,
    title,
    mimeType,
    size,
    metadata,
    recActive,
    recCreateTime,
    recUpdateTime) <> (Video.tupled, Video.unapply)
}