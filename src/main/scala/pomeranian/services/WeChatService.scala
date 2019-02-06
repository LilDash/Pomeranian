package pomeranian.services

import java.security.AlgorithmParameters
import javax.crypto.Cipher

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ HttpMethods, HttpRequest, Uri }
import akka.http.scaladsl.unmarshalling.Unmarshal
import pomeranian.utils.{ AesCryptoUtil, AppConfiguration }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.igl.jwt.Algorithm
import pomeranian.models.wechat.{ WeChatApiResponseJsonProtocol, WeChatDecryptedUserInfo, WeChatDecryptedUserInfoJsonProtocol, WeChatJscode2SessionResponse }
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WeChatService {
  def decryptUserInfo(code: String, encryptedData: String, iv: String)(implicit system: ActorSystem): Future[Option[WeChatDecryptedUserInfo]]
}

class WeChatServiceImpl extends WeChatService with WeChatApiResponseJsonProtocol with WeChatDecryptedUserInfoJsonProtocol {

  override def decryptUserInfo(code: String, encryptedData: String, iv: String)(implicit system: ActorSystem): Future[Option[WeChatDecryptedUserInfo]] = {
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    val appId = AppConfiguration.wechatAppId
    val secret = AppConfiguration.wechatSecret
    val uri = Uri(s"https://api.weixin.qq.com/sns/jscode2session?appid=${appId}&secret=${secret}&js_code=${code}&grant_type=authorization_code")
    for {
      response <- Http().singleRequest(HttpRequest(method = HttpMethods.GET, uri = uri))
      decryptedData <- Unmarshal(response.entity).to[String].map { apiResponse =>
        val converted = apiResponse.parseJson.convertTo[WeChatJscode2SessionResponse]
        val decrypted = AesCryptoUtil.decrypt(encryptedData, converted.session_key.getOrElse(""), iv)
        decrypted match {
          case None => None
          case _ => Option(decrypted.get.parseJson.convertTo[WeChatDecryptedUserInfo])
        }
      }
    } yield decryptedData
  }
}
