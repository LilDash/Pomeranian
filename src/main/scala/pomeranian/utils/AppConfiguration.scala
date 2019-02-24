package pomeranian.utils

import com.typesafe.config.ConfigFactory

object AppConfiguration {
  private val config = ConfigFactory.load()

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

  // InfluxDB
  val influxDbConfig = dbConfig.getConfig("influxDb")
  val influxDbHost = influxDbConfig.getString("host")
  val influxDbPort = influxDbConfig.getInt("port")
  val influxDbUsername = influxDbConfig.getString("username")
  val influxDbPassword = influxDbConfig.getString("password")
  val influxDbName = influxDbConfig.getString("dbname")

  // Upload
  private val uploadConfig = config.getConfig("upload")
  val uploadFileMaxLength = uploadConfig.getInt("fileMaxLength")
  val uploadPolicyExpireTime = uploadConfig.getInt("uploadPolicyExpireTime")

  // Authorization
  private val authConfig = config.getConfig("auth")
  val authType = authConfig.getString("type")
  val authAlg = authConfig.getString("algorithm")
  val authSecretKey = authConfig.getString("secretKey")
  val authValidTime = authConfig.getInt("validTime")

  // WeChat
  private val wechatConfig = config.getConfig("wechat")
  val wechatAppId = wechatConfig.getString("appid")
  val wechatSecret = wechatConfig.getString("secret")
}
