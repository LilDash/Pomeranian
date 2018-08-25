package pomeranian.services

import javax.activation.{MimeType, MimeTypeParseException}

import pomeranian.models.video.VideoInfo
import pomeranian.models.{OssUploadPolicy, UploadNotification}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait UploadService {
  def getOssUploadPolicy(): Future[OssUploadPolicy]
  def saveUploadNotification(notification: UploadNotification): Future[Long]
}

class UploadServiceImpl extends UploadService {
  override def getOssUploadPolicy(): Future[OssUploadPolicy] = {
    val ossService = new OssServiceImpl()
    ossService.getUploadPolicy()
  }

  override def saveUploadNotification(notification: UploadNotification): Future[Long] = {
    if (!notification.validate.isValid) {
      Future.failed(new IllegalArgumentException("Invalid parameters"))
    } else {
      try {
        val mimeType = new MimeType(notification.mimeType);
        mimeType.getPrimaryType match {
          case "video" =>
            val videoInfo = VideoInfo(notification.key, notification.storage,
              notification.title, mimeType.getBaseType, notification.size, "")
            val videoService = new VideoServiceImpl
            videoService.createVideo(videoInfo).flatMap {
                case 0 => Future.successful(0)
                case videoId =>
                  val videoReviewService = new VideoReviewServiceImpl()
                  videoReviewService.createVideoReview(videoId)
                  Future.successful(videoId)
            }
          case _ =>
            throw new UnsupportedOperationException("Unsupported MimeType")
        }
      } catch {
        case ex: MimeTypeParseException =>
          Future.failed(new IllegalArgumentException("Invalid MimeType"))
        case ex: UnsupportedOperationException =>
          Future.failed(new IllegalArgumentException("Unsupported MimeType"))
      }

    }
  }
}