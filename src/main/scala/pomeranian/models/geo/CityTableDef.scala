package pomeranian.models.geo

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class CityTableDef(tag: Tag) extends Table[City](tag, "city") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name")
  def countryId = column[Int]("country_id")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    name,
    displayName,
    countryId,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
  ) <> (City.tupled, City.unapply)
}
