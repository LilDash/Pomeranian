package pomeranian.models.wechat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class WeChatJscode2SessionResponse (
  openid:	Option[String],
  session_key:	Option[String],
  unionid: Option[String],
)

trait WeChatApiResponseJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val weChatJscode2SessionResponseFormat = jsonFormat3(WeChatJscode2SessionResponse)
}
