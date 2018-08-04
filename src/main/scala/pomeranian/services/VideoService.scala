package pomeranian.services

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{ fileUpload, onSuccess }
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.stream.{ ActorMaterializer, IOResult }
import akka.stream.scaladsl.{ FileIO, Source }
import akka.util.ByteString
import pomeranian.models.VideoInfo
import pomeranian.utils.PredefinedPath

import scala.concurrent.Future
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

trait VideoService {
  def handleUpload(fileInfo: FileInfo, fileStream: Source[ByteString, Any]): Future[VideoInfo]
}

class VideoServiceImpl extends VideoService {

  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def handleUpload(fileInfo: FileInfo, fileStream: Source[ByteString, Any]): Future[VideoInfo] = {
    val savePath = "./"
    val path = Paths.get(savePath)
    val path1 = path.resolve(fileInfo.fileName)
    val sink = FileIO.toPath(path1)
    val future = fileStream.runWith(sink)

    future.map { res: IOResult =>
      res.status match {
        case Success(_) => VideoInfo("thisiskey")
        case Failure(e) => throw e
      }
    }
  }
}
