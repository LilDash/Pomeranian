package pomeranian.models.user

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


class UserAuthBindingTableDef(tag: Tag) extends Table[UserAuthBinding](tag, "user_auth_binding") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user_id")
  def authTypeId = column[Int]("auth_type_id")
  def username = column[String]("username")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    userId,
    authTypeId,
    username,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
  ) <> (UserAuthBinding.tupled, UserAuthBinding.unapply)
}