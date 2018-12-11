package pomeranian.models.user

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class UserContactTableDef(tag: Tag) extends Table[UserContact](tag, "user_contact") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user_id")
  def contactTypeId = column[Int]("contact_type_id")
  def contactTypeValue = column[String]("contact_type_value")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    userId,
    contactTypeId,
    contactTypeValue,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
  ) <> (UserContact.tupled, UserContact.unapply)
}


class ContactTypeTableDef(tag: Tag) extends Table[ContactType](tag, "contact_type") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    name,
    displayName,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
  ) <> (ContactType.tupled, ContactType.unapply)
}
