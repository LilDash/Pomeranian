package pomeranian.models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class WeChatMiniProgramLoginRequest (
                                          code: String,
                                          encryptedData: String,
                                          iv: String,
                                        )

trait LoginRequestJsonProtocol extends SprayJsonSupport with CommonJsonProtocol{
  implicit val weChatMiniProgramRequestFormat = jsonFormat3(WeChatMiniProgramLoginRequest)
}
