package pomeranian.services

import java.security.InvalidParameterException

import pomeranian.constants.ErrorCode
import pomeranian.models.trip.{Trip, TripDetail, TripInfo, TripSummary}
import pomeranian.models.user.{User, UserContactInfo, UserInfo}
import pomeranian.repositories.{TripRepository, UserRepository}
import pomeranian.constants.{ContactType => ConstantContactType}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait TripService {
  def getTripDetailById(id: Int): Future[Option[TripDetail]]
  def searchTripsByLocation(
                             departureCountryId: Int,
                             departureCityId: Int,
                             arrivalCountryId: Int,
                             arrivalCityId: Int,
                             offset: Int,
                             num: Int): Future[Seq[TripSummary]]
  def createTrip(trip: Trip): Future[Int]
  def getTripsByUserId(userId: Int, offset: Int, num: Int): Future[Seq[TripInfo]]
  def deleteTrip(userId: Int, tripId: Int): Future[Int]
}

class TripServiceImpl extends  TripService {

  override def getTripDetailById(id: Int): Future[Option[TripDetail]] = {
    TripRepository.fetchTripById(id).collect {
      case tripInfo: Some[TripInfo] =>
        Option(toTripDetail(tripInfo.get))
      case _ => None
    }
  }

  override def searchTripsByLocation(
                                      departureCountryId: Int,
                                      departureCityId: Int,
                                      arrivalCountryId: Int,
                                      arrivalCityId: Int,
                                      offset: Int,
                                      num: Int): Future[Seq[TripSummary]] = {

    (departureCountryId, departureCityId, arrivalCountryId, arrivalCityId) match {
      case (0, 0, 0, 0) => TripRepository.fetchAllTrips(offset, num)
          .map(toTripSummary(_))
      case (_, depCityId, _, arrCityId) if depCityId > 0 && arrCityId > 0 =>
        TripRepository.fetchTripsByDepartureCityAndArrivalCity(depCityId, arrCityId, offset, num)
          .map(toTripSummary(_))
      case (_, depCityId, 0, 0) if depCityId > 0 =>
        TripRepository.fetchTripsByDepartureCity(depCityId, offset, num)
          .map(toTripSummary(_))
      case (0, 0, _, arrCityId) if arrCityId > 0 =>
        TripRepository.fetchTripsByArrivalCity(arrCityId, offset, num)
          .map(toTripSummary(_))
      case (depCountryId, 0, _, arrCityId) if depCountryId > 0 && arrCityId > 0 =>
        TripRepository.fetchTripsByDepartureCountryAndArrivalCity(depCountryId, arrCityId, offset, num)
          .map(toTripSummary(_))
      case (_, depCityId, arrCountryId, 0) if depCityId > 0 && arrCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCityAndArrivalCountry(depCityId, arrCountryId, offset, num)
          .map(toTripSummary(_))
      case (depCountryId, 0, arrCountryId, 0) if depCountryId > 0 && arrCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCountryAndArrivalCountry(depCountryId, arrCountryId, offset, num)
          .map(toTripSummary(_))
      case (depCountryId, 0, 0, 0) if depCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCountry(depCountryId, offset, num)
          .map(toTripSummary(_))
      case (0, 0, arrCountryId, 0) if arrCountryId > 0 =>
        TripRepository.fetchTripsByArrivalCountry(arrCountryId, offset, num)
          .map(toTripSummary(_))
      case _ =>
        throw new InvalidParameterException(s"Invalid parameters - " +
          s"departureCountryId: $departureCountryId " +
          s"departureCityId: $departureCityId " +
          s"arrivalCountryId: $arrivalCountryId " +
          s"arrivalCityId: $arrivalCityId")
    }
  }

  def toTripSummary(tripInfos: Seq[TripInfo]): Seq[TripSummary] = {
    tripInfos.map { tripInfo =>
      val futureUserInfo = UserRepository.fetchUser(tripInfo.userId).collect {
        case user: Some[User] =>
          val u = user.get
          UserInfo(u.id, u.username, u.nickname, u.rating, u.tripsNum, u.avatar)
        case None =>
          UserInfo(0, "", "", 0, 0, None)
      }
      val userInfo = Await.result(futureUserInfo, Duration.Inf)
      TripSummary(tripInfo, userInfo)
    }
  }

  def toTripDetail(tripInfo: TripInfo): TripDetail = {
    val futureUserInfo = UserRepository.fetchUser(tripInfo.userId).collect {
      case user: Some[User] =>
        val u = user.get
        UserInfo(u.id, u.username, u.nickname, u.rating, u.tripsNum, u.avatar)
      case None =>
        UserInfo(0, "", "", 0, 0, None)
    }

//    val futureUserContactInfo = UserRepository.fetchUserContactInfo(tripInfo.userId)
//
//    val result =
//      Await.result(Future.sequence(Seq(futureUserInfo, futureUserContactInfo)), Duration.Inf)
//
//    TripDetail(tripInfo, result(0).asInstanceOf[UserInfo], result(1).asInstanceOf[Seq[UserContactInfo]])

    val result =
      Await.result(Future.sequence(Seq(futureUserInfo)), Duration.Inf)

    TripDetail(tripInfo, result(0))
  }

  override def createTrip(trip: Trip): Future[Int] = {
    if (trip.departureCityId == 0 || trip.arrivalCityId == 0 || trip.departureCityId == trip.arrivalCityId) {
      throw new InvalidParameterException("Invalid departure city and arrival city")
    }
    if (trip.departureTime.after(trip.pickupTime)) {
      throw new InvalidParameterException("Invalid departure time and pickup time")
    }
    if (!"^[0-9a-zA-Z]+$".r.pattern.matcher(trip.flightNumber).matches()){
      throw new InvalidParameterException("Invalid flight number")
    }
    val availableContactTypeId = List(ConstantContactType.WeChatId) // support wechat only for now
    if (!availableContactTypeId.contains(trip.contactTypeId)) {
      throw new InvalidParameterException("Invalid contact type")
    }
    if (!"^[-_0-9a-zA-Z]+$".r.pattern.matcher(trip.contactValue).matches()){
      throw new InvalidParameterException("Invalid contact value")
    }
    if (trip.memo.size > 200) {
      throw new InvalidParameterException("Illegal length of memo")
    }
    TripRepository.insert(trip)
  }

  override def getTripsByUserId(userId: Int, offset: Int, num: Int): Future[Seq[TripInfo]] = {
    TripRepository.fetchTripsByUserId(userId, offset, num)
  }

  override def deleteTrip(userId: Int, tripId: Int): Future[Int] = {
    TripRepository.fetchTripById(tripId).flatMap {
      case trip: Some[TripInfo] =>
        if (trip.get.userId == userId) {
          TripRepository.deactiveTrip(tripId)
        } else {
          Future.successful(0)
        }
      case _ =>
        Future.successful(0)
    }
  }
}
