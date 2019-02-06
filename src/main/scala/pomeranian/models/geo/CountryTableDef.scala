package pomeranian.models.geo

import java.sql.Timestamp
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class CountryTableDef(tag: Tag) extends Table[Country](tag, "country") {
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
    recUpdatedWhen) <> (Country.tupled, Country.unapply)
}
