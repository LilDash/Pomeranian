package pomeranian.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

final case class VideoInfo(key: String, title: String)

trait VideoJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val videoInfoFormat = jsonFormat2(VideoInfo)
}