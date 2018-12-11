package pomeranian.models.user


import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class UserTableDef(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def nickname = column[String]("nickname")
  def rating = column[Int]("rating")
  def tripsNum = column[Int]("trips_num")
  def avatar = column[Option[String]]("avatar")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    username,
    nickname,
    rating,
    tripsNum,
    avatar,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
  ) <> (User.tupled, User.unapply)
}
