package pomeranian.utils

import java.security.MessageDigest

object HashingUtil {
  def md5(str: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    digest.digest(str.getBytes).map("%02x".format(_)).mkString
  }
}