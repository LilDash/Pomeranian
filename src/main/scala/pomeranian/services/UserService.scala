package pomeranian.services

import pomeranian.models.user.{User, UserAuthBinding, UserInfo}
import pomeranian.repositories.UserRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserService {
  def findUserByAuthType(authType: Int, username: String): Future[Option[UserInfo]]
  def findUserByUserId(userId: Int): Future[Option[UserInfo]]
}

class UserServiceImpl extends UserService {

  override def findUserByAuthType(authType: Int, username: String): Future[Option[UserInfo]] = {
    for {
      authBinding <- UserRepository.fetchUserAuthBinding(authType, username)
      userInfo <- getUserInfoByAuthBinding(authBinding)
    } yield userInfo
  }

  override def findUserByUserId(userId: Int): Future[Option[UserInfo]] = {
    UserRepository.fetchUser(userId).map {
      case user: Some[User] =>
        val u = user.get
        val userInfo = UserInfo(u.id, u.username, u.nickname, u.rating, u.tripsNum, u.avatar)
        Option(userInfo)
      case None =>
        // TODO: log
        None
    }
  }

  private def getUserInfoByAuthBinding(authBinding: Option[UserAuthBinding]): Future[Option[UserInfo]] = authBinding match {
    case a: Some[UserAuthBinding] => findUserByUserId(a.get.userId)
    case None => Future.successful(None)
  }
}
