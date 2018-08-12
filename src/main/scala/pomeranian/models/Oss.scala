package pomeranian.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

final case class UploadPolicy(
                               accessId: String,
                               policy: String,
                               signature: String,
                               key: String,
                               host: String,
                               expire: String,
                               callback: String,
                             )

final case class CallbackParams(
                                 callbackUrl: String,
                                 callbackBody: String,
                                 callbackBodyType: String,
                               )

trait OssJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val uploadPolicyJsonFormat = jsonFormat7(UploadPolicy)
  implicit val callbackParamsJsonFormat = jsonFormat3(CallbackParams)
}


