package pomeranian.models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.user.{ ContactTypePair, UserContactJsonProtocol }
import pomeranian.utils.CommonJsonProtocol

//case class WeChatDecryptUserInfoRequest (
//                                code: String,
//                                encryptedData: String,
//                                iv: String,
//                              )

case class SaveContactsRequest(
  userId: Int,
  contacts: Seq[ContactTypePair])

trait UserRequestJsonProtocol extends SprayJsonSupport with UserContactJsonProtocol with CommonJsonProtocol {
  //  implicit val weChatDecryptUserInfoRequestFormat = jsonFormat3(WeChatDecryptUserInfoRequest)
  implicit val saveContactsRequestFormat = jsonFormat2(SaveContactsRequest)
}
