package pomeranian.repositories

import pomeranian.models.video.{Video, VideoTableDef}
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait VideoRepository {

  /**
    *
    * @param video
    * @return id which is larger than 0 if inserted, 0 if failed.
    */
  def insert(video: Video): Future[Long]

//  /**
//    *
//    * @param video
//    * @return None if updated, Some(Video) if row inserted
//    */
//  def save(video: Video): Future[Option[Video]]
}


object VideoRepository extends VideoRepository {
  val db = MySqlDbConnection.db

  val videos = TableQuery[VideoTableDef]

  override def insert(video: Video): Future[Long] = {
    val action = (videos returning videos.map(_.id)) += video
    db.run(action).map(videoId =>
      videoId
    ).recover {
      case ex: Exception =>
        //Logger.error(ex.getCause.getMessage())
        0
    }
  }

//  override def save(video: Video): Future[Option[Video]] = {
//    val videoRow = video.copy(recUpdateTime = (TimeUtil.timeStamp()))
//    val action = (videos returning videos).insertOrUpdate(videoRow)
//    db.run(action).map(res =>
//      res
//    )
//  }
}
