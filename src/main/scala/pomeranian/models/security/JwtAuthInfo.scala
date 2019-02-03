package pomeranian.models.security

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

case class JwtAuthInfo(
                    token: String,
                    expirationTime: Long, // In seconds
                    )

trait JwtAuthInfoJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val jwtAuthInfoResponseFormat = jsonFormat2(JwtAuthInfo)
}
