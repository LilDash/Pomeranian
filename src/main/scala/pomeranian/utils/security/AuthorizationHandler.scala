package pomeranian.utils.security

import akka.http.scaladsl.model.HttpRequest
import io.igl.jwt._
import pomeranian.models.security.{AuthToken, Role}
import pomeranian.utils.{AppConfiguration, TimeUtil}
import scala.util.Try

class AuthorizationHandler {
  val authAlg = getAlg()
  val jwtHeader = Seq(Alg(authAlg), Typ(AppConfiguration.authType))
  val authorizationKey = "Authorization"
  val authSecretKey = AppConfiguration.authSecretKey
  val authValidTime = AppConfiguration.authValidTime

  def getAlg(): Algorithm = {
    AppConfiguration.authAlg match {
      case "HS256" => Algorithm.HS256
      case "HS384" => Algorithm.HS384
      case "HS512" => Algorithm.HS512
      case "RS256" => Algorithm.RS256
      case _ => throw new IllegalArgumentException("Unsupported authorization algorithm")
    }
  }

  def createToken(userId: String, role: Role): AuthToken = {
    val expirationTime = TimeUtil.now/1000 + authValidTime
    val jwt = new DecodedJwt(jwtHeader, Seq(Iss(userId), Aud(role.name), Exp(expirationTime)))
    val token = jwt.encodedAndSigned(authSecretKey)
    AuthToken(token, expirationTime)
  }

  def getAuthToken(req: HttpRequest): String = {
    val authHeader = req.getHeader(authorizationKey)
    if (authHeader != null && authHeader.isPresent) {
      authHeader.get().value()
    } else {
      ""
    }
  }

  def isVerify(req: HttpRequest, role: Role): Boolean = {
    val authToken = getAuthToken(req)
    val res: Try[Jwt] = DecodedJwt.validateEncodedJwt(
      authToken,
      authSecretKey,
      authAlg,
      Set(Typ),
      Set(Iss, Aud, Exp),
      aud = Some(Aud(role.name))
    )
    res.isSuccess
  }
}
