package pomeranian.services

import java.util.{Base64, Date}

import com.aliyun.oss.{ClientConfiguration, OSSClient}
import com.aliyun.oss.common.auth.CredentialsProviderFactory
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.{MatchMode, PolicyConditions}
import pomeranian.models.{CallbackParams, OssJsonProtocol, UploadPolicy}
import pomeranian.utils.{AppConfiguration, HashingUtil}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait OssService {
  //def GetStsToken()
  def GetUploadPolicy(): Future[UploadPolicy];
}
class OssServiceImpl extends OssService with OssJsonProtocol{

  override def GetUploadPolicy(): Future[UploadPolicy] = {

    val accessId = AppConfiguration.ossAccessId
    val accessKey = AppConfiguration.ossAccessKey
    val bucket = AppConfiguration.ossBucket
    val endpoint = AppConfiguration.ossEndpoint
    val key = newKey()
    val host = s"http://$bucket.$endpoint"
    val callbackUrl = getCallbackUrl()

    val credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(accessId, accessKey)
    val client: OSSClient = new OSSClient(endpoint, credentialsProvider, new ClientConfiguration())

    Future {
      try {
        val callbackParams = CallbackParams(
          callbackUrl,
          "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}",
          "application/x-www-form-urlencoded"
        )
        val encoder = Base64.getEncoder()
        val base64CallbackBody = encoder.encode(callbackParams.toJson.toString.getBytes).toString

        val expireEndTime = System.currentTimeMillis() + AppConfiguration.uploadPolicyExpireTime * 1000
        val expiration = new Date(expireEndTime)
        val policyConditions: PolicyConditions = new PolicyConditions()
        policyConditions.addConditionItem(
          PolicyConditions.COND_CONTENT_LENGTH_RANGE,
          0, AppConfiguration.uploadFileMaxLength)
        policyConditions.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, key)

        val postPolicy = client.generatePostPolicy(expiration, policyConditions)
        val binaryData = postPolicy.getBytes("utf-8")
        val encodedPolicy = BinaryUtil.toBase64String(binaryData)
        val postSignature = client.calculatePostSignature(postPolicy)

        val uploadPolicy = UploadPolicy(
          accessId,
          encodedPolicy,
          postSignature,
          key,
          host,
          (expireEndTime / 1000).toString,
          base64CallbackBody,
        )
        uploadPolicy
      } catch {
        case ex: Exception => {
          // TODO: log
          throw ex;
        }
      }
    }
  }

  private def getCallbackUrl(): String = {
      val host = AppConfiguration.httpHost
      if (AppConfiguration.useHttps) {
        val port = AppConfiguration.httpsPort
        s"https://$host.$port"
      } else {
        val port = AppConfiguration.httpPort
        s"http://$host.$port"
      }
  }

  private def newKey(): String = {
    val random = scala.util.Random
    val randomNumber = random.nextInt(10000)
    val time = System.currentTimeMillis()
    val str = s"$time$randomNumber"
    val prefix = HashingUtil.md5(str).substring(1, 6)
    s"$prefix$str"
  }
}
