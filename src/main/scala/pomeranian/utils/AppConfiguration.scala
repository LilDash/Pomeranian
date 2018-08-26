package pomeranian.utils

import com.typesafe.config.ConfigFactory

object AppConfiguration {
  private val config = ConfigFactory.load("config/application")

  // Http
  private val httpConfig = config.getConfig("http")
  val httpHost = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("httpPort")
  val httpsPort = httpConfig.getInt("httpsPort")
  val useHttps = httpConfig.getBoolean("useHttps")

  // CORS
  val corsConfig = config.getConfig("akka-http-cors")

  // Database
  private val dbConfig = config.getConfig("db")
  val dbMysqlConfig = dbConfig.getConfig("mysql")

  // OSS
  private val ossConfig = config.getConfig("oss")
  val ossEndpoint = ossConfig.getString("endpoint")
  val ossAccessId = ossConfig.getString("accessId")
  val ossAccessKey = ossConfig.getString("accessKey")
  val ossBucket = ossConfig.getString("bucket")

  // Upload
  private val uploadConfig = config.getConfig("upload")
  val uploadFileMaxLength = uploadConfig.getInt("fileMaxLength")
  val uploadPolicyExpireTime = uploadConfig.getInt("uploadPolicyExpireTime")
}
