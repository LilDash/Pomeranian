package pomeranian.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

final case class OssUploadPolicy(
                               accessId: String,
                               policy: String,
                               signature: String,
                               key: String,
                               host: String,
                               expire: String,
                               callback: String,
                             )

final case class OssCallbackParams(
                                 callbackUrl: String,
                                 callbackBody: String,
                                 callbackBodyType: String,
                               )

trait OssJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val ossUploadPolicyJsonFormat = jsonFormat7(OssUploadPolicy)
  implicit val ossCallbackParamsJsonFormat = jsonFormat3(OssCallbackParams)
}


