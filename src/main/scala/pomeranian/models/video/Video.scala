package pomeranian.models.video

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class Video(
                            id: Long,
                            key: String,
                            storage: String,
                            title: String,
                            mimeType: String,
                            size: Long,
                            metadata: String,
                            recActive: Int,
                            recCreateTime: Timestamp,
                            recUpdateTime: Timestamp,
                          )

final case class VideoInfo(
                        key: String,
                        storage: String,
                        title: String,
                        mimeType: String,
                        size: Int,
                        metadata: String,
                      )

trait VideoJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val videoInfoFormat = jsonFormat6(VideoInfo)
}