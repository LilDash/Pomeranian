package pomeranian.models.wechat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class WeChatDecryptedUserInfo (
                                   unionId: Option[String],
                                   openId: String,
                                   nickName: String,
                                   gender: Int,
                                   language: String,
                                   city: String,
                                   province: String,
                                   country: String,
                                   avatarUrl: String,
                                   )

trait WeChatDecryptedUserInfoJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val weChatDecryptedUserInfoFormat = jsonFormat9(WeChatDecryptedUserInfo)
}
