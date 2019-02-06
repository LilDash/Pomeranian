package pomeranian.services

import pomeranian.constants.Global
import pomeranian.models.user._
import pomeranian.repositories.UserRepository
import pomeranian.utils.TimeUtil
import pomeranian.constants.{ ContactType => ConstantContactType }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserService {
  def findUserByAuthType(authType: Int, username: String): Future[Option[UserInfo]]
  def findUserByUserId(userId: Int): Future[Option[UserInfo]]
  //  def getContacts(userId: Int): Future[Seq[UserContactInfo]]
  //  def saveContacts(userId: Int, contacts: Seq[ContactTypePair]): Future[Boolean]
  //  def isAtLeastOneValidContact(userId: Int): Future[Boolean]
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

  //  override def getContacts(userId: Int): Future[Seq[UserContactInfo]] = {
  //    UserRepository.fetchUserContactInfo(userId)
  //  }

  //
  //  override def saveContacts(userId: Int, contacts: Seq[ContactTypePair]): Future[Boolean] = {
  //    val time = TimeUtil.timeStamp()
  //    val availableContactTypeId = List(ConstantContactType.Email, ConstantContactType.PhoneNumber, ConstantContactType.WeChatId)
  //    val boolList = contacts.map {
  //      case pair: ContactTypePair =>
  //        if (availableContactTypeId.contains(pair.contactTypeId)) {
  //          val contact = UserContact(0, userId, pair.contactTypeId, pair.contactValue, Global.DbRecActive, time, time)
  //          UserRepository.saveUserContact(contact).map { res =>
  //            res > 0
  //          }
  //        } else {
  //          Future.successful(false)
  //        }
  //    }
  //    Future.sequence(boolList).map {l =>
  //        !l.toList.contains(false)
  //    }
  //  }

  //  override def isAtLeastOneValidContact(userId: Int): Future[Boolean] = {
  //    UserRepository.fetchUserContactInfo(userId).map { contacts =>
  //      contacts.exists(c => !c.contactTypeValue.isEmpty)
  //    }
  //  }

  private def getUserInfoByAuthBinding(authBinding: Option[UserAuthBinding]): Future[Option[UserInfo]] = authBinding match {
    case a: Some[UserAuthBinding] => findUserByUserId(a.get.userId)
    case None => Future.successful(None)
  }
}
