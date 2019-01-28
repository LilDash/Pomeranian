package pomeranian.repositories

import pomeranian.constants.Global
import pomeranian.models.geo.{CityTableDef, CountryTableDef}
import pomeranian.models.trip.{Trip, TripInfo, TripTableDef}
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TripRepository {
  def fetchTripById(id: Int): Future[Option[TripInfo]]

  def fetchAllTrips(offset: Int, num: Int): Future[Seq[TripInfo]]

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

  def fetchTripsByDepartureCountry(
                                    departureCountryId: Int,
                                    offset: Int,
                                    num: Int): Future[Seq[TripInfo]]

  def fetchTripsByDepartureCity(
                                    departureCityId: Int,
                                    offset: Int,
                                    num: Int): Future[Seq[TripInfo]]

  def fetchTripsByArrivalCountry(
                                 arrivalCountryId: Int,
                                 offset: Int,
                                 num: Int): Future[Seq[TripInfo]]

  def fetchTripsByArrivalCity(
                                 arrivalCityId: Int,
                                 offset: Int,
                                 num: Int): Future[Seq[TripInfo]]

  def insert(t: Trip): Future[Int]

  def fetchTripsByUserId(
                        userId: Int,
                        offset: Int,
                        num: Int
                        ): Future[Seq[TripInfo]]
}

object TripRepository extends TripRepository {
  val db = MySqlDbConnection.db

  val trip = TableQuery[TripTableDef]
  val country = TableQuery[CountryTableDef]
  val city = TableQuery[CityTableDef];

  override def fetchTripById(id: Int): Future[Option[TripInfo]] = {
    val selectedTrips = trip.filter(_.id === id)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.result.headOption.map { rows =>
        rows.collect {
          case ((((t, depCity), depCountry), arrCity), arrCountry) =>
            TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
              t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
              t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
              t.memo, t.recCreatedWhen)
        }
      }
    db.run(query)
  }

  override def fetchAllTrips(offset: Int, num: Int): Future[Seq[TripInfo]] = {
    val join1 = (trip join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.filter(_._1._1._1._1.recStatus === Global.DbRecActive)
      .sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num)
      .result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCityAndArrivalCity(departureCityId: Int,
                                                   arrivalCityId: Int,
                                                   offset: Int,
                                                   num: Int): Future[Seq[TripInfo]] = {

    val selectedTrips = trip.filter(_.departureCityId === departureCityId)
      .filter(_.arrivalCityId === arrivalCityId)
      .filter(_.recStatus === Global.DbRecActive)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
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
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join selectedDepartureCountry on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
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
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join selectedArrivalCountry on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
        .drop(offset).take(num).result.map { rows =>
        rows.collect {
          case ((((t, depCity), depCountry), arrCity), arrCountry) =>
            TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
              t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
              t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
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
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join selectedDepCountry on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join selectedArrCountry on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCountry(
                                    departureCountryId: Int,
                                    offset: Int,
                                    num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.recStatus === Global.DbRecActive)
    val selectedDepartureCountry = country.filter(_.id === departureCountryId)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join selectedDepartureCountry on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByDepartureCity(
                                 departureCityId: Int,
                                 offset: Int,
                                 num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.departureCityId === departureCityId)
      .filter(_.recStatus === Global.DbRecActive)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByArrivalCountry(
                                  arrivalCountryId: Int,
                                  offset: Int,
                                  num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.recStatus === Global.DbRecActive)
    val selectedArrCountry = country.filter(_.id === arrivalCountryId)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join selectedArrCountry on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def fetchTripsByArrivalCity(
                               arrivalCityId: Int,
                               offset: Int,
                               num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.arrivalCityId === arrivalCityId)
      .filter(_.recStatus === Global.DbRecActive)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

  override def insert(t: Trip): Future[Int] = {
    val action = (trip returning trip.map(_.id)) += t
    db.run(action).map(tripId =>
      tripId).recover {
      case ex: Exception =>
        //Logger.error(ex.getCause.getMessage())
        0
    }
  }

  override def fetchTripsByUserId(userId: Int, offset: Int, num: Int): Future[Seq[TripInfo]] = {
    val selectedTrips = trip.filter(_.userId === userId)
      .filter(_.recStatus === Global.DbRecActive)
    val join1 = (selectedTrips join city on (_.departureCityId === _.id)) join country on (_._2.countryId === _.id)
    val join2 = (join1 join city on (_._1._1.arrivalCityId === _.id)) join country on (_._2.countryId === _.id)
    val query = join2.sortBy { t => t._1._1._1._1.id.desc }
      .drop(offset).take(num).result.map { rows =>
      rows.collect {
        case ((((t, depCity), depCountry), arrCity), arrCountry) =>
          TripInfo(t.id, t.userId, t.departureCityId, depCity.displayName, depCountry.id, depCountry.displayName,
            t.arrivalCityId, arrCity.displayName, arrCountry.id, arrCountry.displayName, t.flightNumber, t.totalCapacity,
            t.remainingCapacity, t.capacityPrice, t.currency, t.departureTime, t.pickupTime, t.contactTypeId, t.contactValue,
            t.memo, t.recCreatedWhen)
      }
    }
    db.run(query)
  }

}
