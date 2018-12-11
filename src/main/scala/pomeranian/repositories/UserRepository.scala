package pomeranian.repositories

import pomeranian.constants.Global
import pomeranian.models.user._
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait UserRepository {
  def fetchUser(userId: Int): Future[Option[User]]

  def fetchUserContactInfo(userId: Int): Future[Seq[UserContactInfo]]
}

object UserRepository extends UserRepository {
  val db = MySqlDbConnection.db

  val user = TableQuery[UserTableDef]
  val contactType = TableQuery[ContactTypeTableDef]
  val userContact = TableQuery[UserContactTableDef]

  override def fetchUser(userId: Int): Future[Option[User]] = {
    val query = user.filter(_.id === userId)
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

  override def fetchUserContactInfo(userId: Int): Future[Seq[UserContactInfo]] = {
    val query = userContact.filter(_.id === userId)
      .filter(_.recStatus === Global.DbRecActive)
      .joinLeft(contactType).on(_.contactTypeId === _.id)
      .result.map { rows =>
      rows.collect {
        case (uc, ct: Some[ContactType]) =>
          UserContactInfo(uc.id, uc.contactTypeId, ct.get.name, ct.get.displayName, uc.contactTypeValue)
      }
    }

    db.run(query)
  }

}