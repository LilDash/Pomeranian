package pomeranian.models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class WeChatDecryptUserInfoRequest (
                                code: String,
                                encryptedData: String,
                                iv: String,
                              )

trait UserRequestJsonProtocol extends SprayJsonSupport with CommonJsonProtocol{
  implicit val weChatDecryptUserInfoRequestFormat = jsonFormat3(WeChatDecryptUserInfoRequest)
}
