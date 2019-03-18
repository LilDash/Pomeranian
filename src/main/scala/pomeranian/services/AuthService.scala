package pomeranian.services

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory
import pomeranian.constants.{ AuthType, ErrorCode, Global }
import pomeranian.models.login.LoginResultStatus.LoginResultStatus
import pomeranian.models.login.{ LoginResultStatus, MiniProgramLoginResult, MiniProgramLoginResultInfo }
import pomeranian.models.requests.WeChatMiniProgramLoginRequest
import pomeranian.models.responses.AuthWeChatMiniLoginResponse
import pomeranian.models.security.Role
import pomeranian.models.user.{ User, UserInfo }
import pomeranian.models.wechat.WeChatDecryptedUserInfo
import pomeranian.repositories.UserRepository
import pomeranian.utils.TimeUtil
import pomeranian.utils.measurement.Measurer
import pomeranian.utils.security.AuthorizationHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AuthService {
  def loginWithWeChatMiniProgram(request: WeChatMiniProgramLoginRequest)(implicit system: ActorSystem): Future[AuthWeChatMiniLoginResponse]
}

class AuthServiceImpl(implicit system: ActorSystem, measurer: Measurer) extends AuthService {
  lazy val logger = LoggerFactory.getLogger(this.getClass)
  lazy val weChatService = new WeChatServiceImpl
  lazy val userService = new UserServiceImpl
  lazy val authHandler = new AuthorizationHandler

  override def loginWithWeChatMiniProgram(request: WeChatMiniProgramLoginRequest)(implicit system: ActorSystem): Future[AuthWeChatMiniLoginResponse] = {
    val futureResult = weChatService.decryptUserInfo(request.code, request.encryptedData, request.iv)(system).flatMap {
      case decrypted: Some[WeChatDecryptedUserInfo] =>
        val weChatUserInfo = decrypted.get
        userService.findUserByAuthType(AuthType.WeChatMiniProgram, weChatUserInfo.openId).flatMap {
          case userInfo: Some[UserInfo] =>
            val u = userInfo.get
            val result = buildMiniProgramLoginResult(u, weChatUserInfo)
            Future.successful(result)
          case None =>
            this.createUserWithAuthTypeWeChatMiniProgram(weChatUserInfo).map { user =>
              buildMiniProgramLoginResult(user, weChatUserInfo)
            }.recover {
              case e =>
                logger.error("Create user failed", e)
                buildMiniProgramLoginFailureResult(LoginResultStatus.CreateUserFailed)
            }
        }
      case _ =>
        logger.debug("Decrypt WeChatUserInfo failed")
        Future.successful(buildMiniProgramLoginFailureResult(LoginResultStatus.DecryptWeChatUserInfoFailed))
    }
    measurer.measure("auth.loginWithWeChatMiniProgram", futureResult)
    buildMiniProgramLoginResponse(futureResult)
  }

  private def createUserWithAuthTypeWeChatMiniProgram(weChatUserInfo: WeChatDecryptedUserInfo): Future[User] = {
    val time = TimeUtil.timeStamp()
    val user = User(0, weChatUserInfo.openId, weChatUserInfo.nickName, 0, 0,
      Option(weChatUserInfo.avatarUrl), false, Global.DbRecActive, time, time)
    UserRepository.insertUserWithBinding(AuthType.WeChatMiniProgram, weChatUserInfo.openId, user).map { userId =>
      user.copy(id = userId)
    }
  }

  private def buildMiniProgramLoginResult(u: UserInfo, weChatUserInfo: WeChatDecryptedUserInfo): MiniProgramLoginResult = {
    val resultInfo = MiniProgramLoginResultInfo(u.id, u.username, u.nickname, u.avatar, u.rating, u.tripsNum, weChatUserInfo.openId)
    val jwtInfo = authHandler.createToken(u.id.toString, Role.Basic)
    MiniProgramLoginResult(LoginResultStatus.Success, Option(resultInfo), Option(jwtInfo))
  }

  private def buildMiniProgramLoginResult(u: User, weChatUserInfo: WeChatDecryptedUserInfo): MiniProgramLoginResult = {
    val resultInfo = MiniProgramLoginResultInfo(u.id, weChatUserInfo.openId,
      weChatUserInfo.nickName, Option(weChatUserInfo.avatarUrl), u.rating, u.tripsNum, weChatUserInfo.openId)
    val jwtInfo = authHandler.createToken(u.id.toString, Role.Basic)
    MiniProgramLoginResult(LoginResultStatus.Success, Option(resultInfo), Option(jwtInfo))
  }

  private def buildMiniProgramLoginFailureResult(status: LoginResultStatus): MiniProgramLoginResult = {
    MiniProgramLoginResult(status, None, None)
  }

  private def buildMiniProgramLoginResponse(futureResult: Future[MiniProgramLoginResult]): Future[AuthWeChatMiniLoginResponse] = {
    futureResult.map { result =>
      AuthWeChatMiniLoginResponse(ErrorCode.Ok, "", result)
    }.recover {
      case ex: Exception =>
        logger.error("loginWithWeChatMiniProgram failure", ex)
        throw ex
    }
  }
}
