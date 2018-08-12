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

  // Mysql
  private val mysqlConfig = config.getConfig("mysql")
  val mysqlDriver = mysqlConfig.getString("driver")
  val mysqlUrl = mysqlConfig.getString("url")
  val mysqlUsername = mysqlConfig.getString("username")
  val mysqlPassword = mysqlConfig.getString("password")
  val mysqlMaxConnections = mysqlConfig.getInt("maxConnections")
  val mysqlInitialConnections = mysqlConfig.getInt("initialConnections")
  val mysqlIncrementalConnections = mysqlConfig.getInt("incrementalConnections")
  val mysqlGetConnectionSleepTime = mysqlConfig.getInt("sleepTime")

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
