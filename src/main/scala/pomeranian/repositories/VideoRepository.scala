package pomeranian.repositories

import pomeranian.models.VideoInfo
import pomeranian.utils.TimeUtil
import pomeranian.utils.database.BaseDao

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object VideoRepository extends BaseDao {

  def createVideo(videoInfo: VideoInfo): Future[Int] = {
    Future {
      val timestamp = TimeUtil.timeStamp()
      insert("video", Map(
        "key" -> videoInfo.key,
        "title" -> videoInfo.title,
        "rec_active" -> 1,
        "rec_createtime" -> timestamp,
        "rec_updatetime" -> timestamp))
    }
  }
}
