package pomeranian.repositories

import pomeranian.constants.{AuthType, Global}
import pomeranian.models.user._
import pomeranian.utils.TimeUtil
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait UserRepository {
  def fetchUser(userId: Int): Future[Option[User]]

//  def fetchUserContactInfo(userId: Int): Future[Seq[UserContactInfo]]

  def fetchUserAuthBinding(authType: Int, username: String): Future[Option[UserAuthBinding]]

  def insertUserWithBinding(authType: Int, username: String, user: User): Future[Int]

//  def saveUserContact(userContact: UserContact): Future[Int]
}

object UserRepository extends UserRepository {
  val db = MySqlDbConnection.db

  lazy val users = TableQuery[UserTableDef]
  lazy val contactType = TableQuery[ContactTypeTableDef]
  lazy val userContact = TableQuery[UserContactTableDef]
  lazy val userAuthBindings = TableQuery[UserAuthBindingTableDef]

  override def fetchUser(userId: Int): Future[Option[User]] = {
    val query = users.filter(_.id === userId)
      .filter(_.recStatus === Global.DbRecActive)
      .result.headOption.map { rows =>
      rows.collect {
        case u =>
          User(u.id, u.username, u.nickname, u.rating, u.tripsNum, u.avatar,
            u.recStatus, u.recCreatedWhen, u.recUpdatedWhen)
      }
    }

    db.run(query)
  }

//  override def fetchUserContactInfo(userId: Int): Future[Seq[UserContactInfo]] = {
//    val query = userContact.filter(_.userId === userId)
//      .filter(_.recStatus === Global.DbRecActive)
//      .joinLeft(contactType).on(_.contactTypeId === _.id)
//      .result.map { rows =>
//      rows.collect {
//        case (uc, ct: Some[ContactType]) =>
//          UserContactInfo(uc.id, uc.contactTypeId, ct.get.name, ct.get.displayName, uc.contactTypeValue)
//      }
//    }
//
//    db.run(query)
//  }

  override def fetchUserAuthBinding(authType: Int, username: String): Future[Option[UserAuthBinding]] = {
    val query = userAuthBindings.filter(_.authTypeId === authType)
        .filter(_.username === username)
        .filter(_.recStatus === Global.DbRecActive)
        .result.headOption.map { rows =>
          rows.collect {
            case r =>
              UserAuthBinding(r.id, r.userId, r.authTypeId, r.username, r.recStatus, r.recCreatedWhen, r.recUpdatedWhen)
          }
        }
    db.run(query)
  }

  override def insertUserWithBinding(authType: Int, username: String, user: User): Future[Int] = {
    val time = TimeUtil.timeStamp()
    val action = (for {
      userId <- users returning users.map(_.id) += user
      binding <- DBIO.successful(UserAuthBinding(0, userId, authType, username, Global.DbRecActive, time, time))
      _ <- userAuthBindings returning userAuthBindings.map(_.id) += binding
    } yield (userId))

    db.run(action.transactionally)
  }

//  override def saveUserContact(contact: UserContact): Future[Int] = {
//    val query = userContact.filter(_.userId === contact.userId)
//      .filter(_.contactTypeId === contact.contactTypeId)
//      .filter(_.recStatus === contact.recStatus)
//      .result.headOption
//    db.run(query).flatMap { row =>
//      if (row.isDefined) {
//        // update
//        val action = userContact
//          .filter(_.id === row.get.id)
//          .map(c => (c.contactTypeValue, c.recUpdatedWhen))
//          .update((contact.contactTypeValue, contact.recUpdatedWhen))
//        db.run(action).map(res => res).recover {
//          case ex: Exception =>
//            //TODO: Logger.error(ex.getCause.getMessage())
//            0
//          }
//      } else {
//        // insert
//        val action = userContact returning userContact.map(_.id) += contact
//        db.run(action).map(contactId => contactId).recover {
//          case ex: Exception =>
//            //TODO: Logger.error(ex.getCause.getMessage())
//            0
//        }
//      }
//    }
//  }
}