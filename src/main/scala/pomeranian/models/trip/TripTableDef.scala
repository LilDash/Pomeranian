package pomeranian.models.trip

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

class TripTableDef(tag: Tag) extends Table[Trip](tag, "trip") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user_id")
  def departureCityId = column[Int]("departure_city_id")
  def arrivalCityId = column[Int]("arrival_city_id")
  def flightNumber = column[String]("flight_number")
  def totalCapacity = column[Int]("total_capacity")
  def remainingCapacity = column[Int]("remaining_capacity")
  def capacityPrice = column[Float]("capacity_price")
  def currency = column[String]("currency")
  def departureTime = column[Timestamp]("departure_time")
  def pickupTime = column[Timestamp]("pickup_time")
  def contactTypeId = column[Int]("contact_type_id")
  def contactValue = column[String]("contact_value")
  def memo = column[Option[String]]("memo")
  def recStatus = column[Int]("rec_status")
  def recCreatedWhen = column[Timestamp]("rec_created_when")
  def recUpdatedWhen = column[Timestamp]("rec_updated_when")

  override def * = (
    id,
    userId,
    departureCityId,
    arrivalCityId,
    flightNumber,
    totalCapacity,
    remainingCapacity,
    capacityPrice,
    currency,
    departureTime,
    pickupTime,
    contactTypeId,
    contactValue,
    memo,
    recStatus,
    recCreatedWhen,
    recUpdatedWhen,
    ) <> (Trip.tupled, Trip.unapply)
}
