package pomeranian.services

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import pomeranian.constants.Global
import pomeranian.models.video.{Video, VideoInfo}
import pomeranian.repositories.VideoRepository
import pomeranian.utils.TimeUtil

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait VideoService {
//  def handleUpload(fileInfo: FileInfo, fileStream: Source[ByteString, Any]): Future[VideoInfo]
  def createVideo(videoInfo: VideoInfo): Future[Long]
}

class VideoServiceImpl extends VideoService {

//  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//
//  def handleUpload(fileInfo: FileInfo, fileStream: Source[ByteString, Any]): Future[VideoInfo] = {
//    val savePath = "./"
//    val path = Paths.get(savePath)
//    val path1 = path.resolve(fileInfo.fileName)
//    val sink = FileIO.toPath(path1)
//    val future = fileStream.runWith(sink)
//
//    //VideoRepository.createVideo(VideoInfo("alala", "oss", "asd", "asd", 1, "Asd"))
//
//    future.map { res: IOResult =>
//      res.status match {
//        case Success(_) => VideoInfo("thisiskey", "oss", "title", "asd", 1, "as")
//        case Failure(e) => throw e
//      }
//    }
//  }
  override def createVideo(videoInfo: VideoInfo): Future[Long] = {
    val timeStamp = TimeUtil.timeStamp()
    val video = Video(0, videoInfo.key, videoInfo.storage, videoInfo.title,
                      videoInfo.mimeType, videoInfo.size, videoInfo.metadata,
                      Global.DbRecNonactive, timeStamp, timeStamp)
    VideoRepository.insert(video)
  }
}
