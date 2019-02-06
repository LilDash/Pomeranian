package pomeranian.utils

import java.security.{ AlgorithmParameters, Security }
import javax.crypto.Cipher
import javax.crypto.spec.{ IvParameterSpec, SecretKeySpec }

import org.apache.commons.codec.binary.Base64
//import org.bouncycastle.jce.provider.BouncyCastleProvider

object AesCryptoUtil {

  def decrypt(data: String, key: String, iv: String, encodingFormat: String = "UTF-8"): Option[String] = {
    val dataBytes = Base64.decodeBase64(data)
    val keyBytes = Base64.decodeBase64(key)
    val ivBytes = Base64.decodeBase64(iv)

    val keySpec = new SecretKeySpec(keyBytes, "AES");
    val parameters = AlgorithmParameters.getInstance("AES");
    parameters.init(new IvParameterSpec(ivBytes));

    //    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, parameters)
    new String(cipher.doFinal(dataBytes))

    val resultBytes = cipher.doFinal(dataBytes);
    if (null != resultBytes && resultBytes.length > 0) {
      val result = new String(resultBytes, encodingFormat)
      Option(result)
    } else {
      None
    }
  }
}
