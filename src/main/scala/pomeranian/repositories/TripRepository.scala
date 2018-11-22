package pomeranian.repositories

import pomeranian.constants.Global
import pomeranian.models.geo.{CityTableDef, CountryTableDef}
import pomeranian.models.trip.{TripInfo, TripTableDef}
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TripRepository {
  def fetchTripsByDepartureCityAndArrivalCity(
    departureCityId: Int,
    arrivalCityId: Int,
    offset: Int,
    num: Int): Future[Seq[TripInfo]]

  def fetchTripsByDepartureCountryAndArrivalCity(
    departureCountryId: Int,
    arrivalCityId: Int,
    offset: Int,
    num: Int): Future[Seq[TripInfo]]

  def fetchTripsByDepartureCityAndArrivalCountry(
    departureCityId: Int,
    arrivalCountryId: Int,
    offset: Int,
    num: Int): Future[Seq[TripInfo]]

  def fetchTripsByDepartureCountryAndArrivalCountry(
    departureCountryId: Int,
    arrivalCountryId: Int,
    offset: Int,
    num: Int): Future[Seq[TripInfo]]
}

object TripRepository extends TripRepository {
  val db = MySqlDbConnection.db

  val trip = TableQuery[TripTableDef]
  val country = TableQuery[CountryTableDef]
  val city = TableQuery[CityTableDef];

  override def fetchTripsByDepartureCityAndArrivalCity(departureCityId: Int,
                                                   arrivalCityId: Int,
                                                   offset: Int,
                                                   num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.departureCityId === departureCityId)
      .filter(_.arrivalCityId === arrivalCityId)
      .filter(_.recStatus === Global.DbRecActive)
      .sortBy { t => t.id.desc }
      .drop(offset).take(num)
    val query = (for {
      ((_, depCity), depCountry) <- (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
      ((t, arrCity), arrCountry) <- (selectedTrips join city on (_.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    } yield (t, depCity, depCountry, arrCity, arrCountry)).result.map { rows =>
      rows.collect {
        case (t, depCity, depCountry, arrCity, arrCountry) =>
          TripInfo(t.id, t.userId, "", "", t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCountryAndArrivalCity(
                                     departureCountryId: Int,
                                     arrivalCityId: Int,
                                     offset: Int,
                                     num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.arrivalCityId === arrivalCityId)
      .filter(_.recStatus === Global.DbRecActive)
    val selectedDepartureCountry = country.filter(_.id === departureCountryId)
    val query = (for {
      ((_, depCity), depCountry) <- (selectedTrips join city on (_.departureCityId === _.id)) join selectedDepartureCountry on (_._2.countryId === _.id)
      ((t, arrCity), arrCountry) <- (selectedTrips join city on (_.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    } yield (t, depCity, depCountry, arrCity, arrCountry)).result.map { rows =>
      rows.collect {
        case (t, depCity, depCountry, arrCity, arrCountry) =>
          TripInfo(t.id, t.userId, "", "", t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCityAndArrivalCountry(
                                                   departureCityId: Int,
                                                   arrivalCountryId: Int,
                                                   offset: Int,
                                                   num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.departureCityId === departureCityId)
      .filter(_.recStatus === Global.DbRecActive)
    val selectedArrivalCountry = country.filter(_.id === arrivalCountryId)
    val query = (for {
      ((_, depCity), depCountry) <- (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
      ((t, arrCity), arrCountry) <- (selectedTrips join city on (_.arrivalCityId === _.id)) join selectedArrivalCountry on (_._2.countryId === _.id)
    } yield (t, depCity, depCountry, arrCity, arrCountry)).result.map { rows =>
      rows.collect {
        case (t, depCity, depCountry, arrCity, arrCountry) =>
          TripInfo(t.id, t.userId, "", "", t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCountryAndArrivalCountry(departureCountryId: Int,
                                                             arrivalCountryId: Int,
                                                             offset: Int,
                                                             num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.recStatus === Global.DbRecActive)
    val selectedDepCountry = country.filter(_.id === departureCountryId)
    val selectedArrCountry = country.filter(_.id === arrivalCountryId)
    val query = (for {
      ((_, depCity), depCountry) <- (selectedTrips join city on (_.departureCityId === _.id)) join selectedDepCountry on (_._2.countryId === _.id)
      ((t, arrCity), arrCountry) <- (selectedTrips join city on (_.arrivalCityId === _.id)) join selectedArrCountry on (_._2.countryId === _.id)
    } yield (t, depCity, depCountry, arrCity, arrCountry)).result.map { rows =>
      rows.collect {
        case (t, depCity, depCountry, arrCity, arrCountry) =>
          TripInfo(t.id, t.userId, "", "", t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }
}
