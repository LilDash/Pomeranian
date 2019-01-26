package pomeranian.services

import akka.actor.ActorSystem
import pomeranian.constants.{AuthType, Global}
import pomeranian.models.login.{LoginResultStatus, MiniProgramLoginResult, MiniProgramLoginResultInfo}
import pomeranian.models.user.{User, UserInfo}
import pomeranian.models.wechat.WeChatDecryptedUserInfo
import pomeranian.repositories.UserRepository
import pomeranian.utils.TimeUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AuthService {
  def loginWithWeChatMiniProgram(code: String, encryptedData: String, iv: String)(implicit system: ActorSystem): Future[MiniProgramLoginResult]
}

class AuthServiceImpl extends AuthService {

  lazy val weChatService = new WeChatServiceImpl
  lazy val userService = new UserServiceImpl

  override def loginWithWeChatMiniProgram(code: String, encryptedData: String, iv: String)(implicit system: ActorSystem): Future[MiniProgramLoginResult] = {
    weChatService.decryptUserInfo(code, encryptedData, iv).flatMap {
      case decrypted: Some[WeChatDecryptedUserInfo] =>
        val weChatUserInfo = decrypted.get
        userService.findUserByAuthType(AuthType.WeChatMiniProgram, weChatUserInfo.openId).flatMap {
          case userInfo: Some[UserInfo] =>
            val u = userInfo.get
            val resultInfo = MiniProgramLoginResultInfo(u.id, u.username, u.nickname, u.avatar, weChatUserInfo.openId)
            Future.successful(MiniProgramLoginResult(LoginResultStatus.Success, Option(resultInfo)))
          case None =>
            // TODO: User not exist, then create new account for it
            val time = TimeUtil.timeStamp()
            val user = User(0, weChatUserInfo.openId, weChatUserInfo.nickName, 0, 0,
              Option(weChatUserInfo.avatarUrl), Global.DbRecActive, time, time)
            UserRepository.insertUserWithBinding(AuthType.WeChatMiniProgram, weChatUserInfo.openId, user).map { userId =>
              val resultInfo = MiniProgramLoginResultInfo(userId, weChatUserInfo.openId,
                weChatUserInfo.nickName, Option(weChatUserInfo.avatarUrl), weChatUserInfo.openId)
              MiniProgramLoginResult(LoginResultStatus.Success, Option(resultInfo))
            }.recover {
              case e =>
                // TODO: log
                println(e.getMessage)
                MiniProgramLoginResult(LoginResultStatus.CreateUserFailed, None)
            }
        }
      case _ =>
        // TODO: log
        Future.successful(MiniProgramLoginResult(LoginResultStatus.DecryptWeChatUserInfoFailed, None))
    }
  }

}
