package pomeranian.services

import java.security.InvalidParameterException
import java.sql.Timestamp

import org.slf4j.LoggerFactory
import pomeranian.constants.{ErrorCode, Global, ContactType => ConstantContactType}
import pomeranian.models.trip.{Trip, TripDetail, TripInfo, TripSummary}
import pomeranian.models.user.{User, UserInfo}
import pomeranian.repositories.{TripRepository, UserRepository}
import pomeranian.models.requests.PublishTripRequest
import pomeranian.models.responses._
import pomeranian.utils.TimeUtil

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait TripService {
  def getTripDetailById(id: Int): Future[GetTripDetailResponse]
  def searchTripsByLocation(
    departureCountryId: Int,
    departureCityId: Int,
    arrivalCountryId: Int,
    arrivalCityId: Int,
    offset: Int,
    num: Int): Future[GetTripsResponse]
  def createTrip(request: PublishTripRequest): Future[PublishTripResponse]
  def getTripsByUserId(userId: Int, offset: Int, num: Int): Future[GetMyTripsResponse]
  def deleteTrip(userId: Int, tripId: Int): Future[SimpleResponse]
}

class TripServiceImpl extends TripService {
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def getTripDetailById(id: Int): Future[GetTripDetailResponse] = {
    val futureResult = TripRepository.fetchTripById(id).collect {
      case tripInfo: Some[TripInfo] =>
        Option(toTripDetail(tripInfo.get))
      case _ => None
    }
    buildGetTripDetailByIdResponse(id, futureResult)
  }

  override def searchTripsByLocation(
    departureCountryId: Int,
    departureCityId: Int,
    arrivalCountryId: Int,
    arrivalCityId: Int,
    offset: Int,
    num: Int): Future[GetTripsResponse] = {

    val futureResult = (departureCountryId, departureCityId, arrivalCountryId, arrivalCityId) match {
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
    buildSearchTripsByLocationResponse(futureResult)
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
    val result =
      Await.result(Future.sequence(Seq(futureUserInfo)), Duration.Inf)

    TripDetail(tripInfo, result(0))
  }

  override def createTrip(request: PublishTripRequest): Future[PublishTripResponse] = {
    val now = TimeUtil.timeStamp()
    val trip = Trip(
      0,
      request.userId,
      request.depCityId,
      request.arrCityId,
      request.flightNo,
      request.totalCapacity,
      request.remainingCapacity,
      request.capacityPrice,
      "CNY", // TODO: by default
      new Timestamp(request.depTime),
      new Timestamp(request.arrTime),
      request.contactTypeId,
      request.contactValue,
      Option(request.memo),
      Global.DbRecActive,
      now,
      now,
    )

    if (trip.departureCityId == 0 || trip.arrivalCityId == 0 || trip.departureCityId == trip.arrivalCityId) {
      throw new InvalidParameterException("Invalid departure city and arrival city")
    }
    if (trip.departureTime.after(trip.pickupTime)) {
      throw new InvalidParameterException("Invalid departure time and pickup time")
    }
    if (!"^[0-9a-zA-Z]+$".r.pattern.matcher(trip.flightNumber).matches()) {
      throw new InvalidParameterException("Invalid flight number")
    }
    val availableContactTypeId = List(ConstantContactType.WeChatId) // support wechat only for now
    if (!availableContactTypeId.contains(trip.contactTypeId)) {
      throw new InvalidParameterException("Invalid contact type")
    }
    if (!"^[-_0-9a-zA-Z]+$".r.pattern.matcher(trip.contactValue).matches()) {
      throw new InvalidParameterException("Invalid contact value")
    }
    if (trip.memo.size > 200) {
      throw new InvalidParameterException("Illegal length of memo")
    }

    val futureResult = TripRepository.insert(trip)
    buildCreateTripResponse(futureResult)
  }

  override def getTripsByUserId(userId: Int, offset: Int, num: Int): Future[GetMyTripsResponse] = {
    val futureResult = TripRepository.fetchTripsByUserId(userId, offset, num)
    buildGetMyTripsResponse(futureResult)
  }

  override def deleteTrip(userId: Int, tripId: Int): Future[SimpleResponse] = {
    val futureResult = TripRepository.fetchTripById(tripId).flatMap {
      case trip: Some[TripInfo] =>
        if (trip.get.userId == userId) {
          TripRepository.deactiveTrip(tripId)
        } else {
          Future.successful(0)
        }
      case _ =>
        Future.successful(0)
    }
    buildDeleteTripResponse(futureResult)
  }

  private def buildDeleteTripResponse(futureResult: Future[Int]): Future[SimpleResponse] = {
    futureResult.collect {
      case result if result > 0 =>
        SimpleResponse(ErrorCode.Ok, "")
      case _ =>
        SimpleResponse(ErrorCode.DeleteTripFailed, "Delete trip failed")
    }.recover {
      case ex: Exception =>
        logger.error("deleteTrip Failure", ex)
        throw ex
    }
  }

  private def buildGetTripDetailByIdResponse(tripId: Int, futureResult: Future[Option[TripDetail]]): Future[GetTripDetailResponse] = {
    futureResult.map { result =>
      if (result.isDefined) {
        GetTripDetailResponse(ErrorCode.Ok, "", result)
      } else {
        GetTripDetailResponse(ErrorCode.TripNotFound, s"Trip id: ${tripId} not found ", result)
      }
    }.recover {
      case ex: Exception =>
        logger.error("getTripDetailById Failure", ex)
        throw ex
    }
  }

  private def buildSearchTripsByLocationResponse(futureResult: Future[Seq[TripSummary]]): Future[GetTripsResponse] = {
    futureResult.map { result =>
      GetTripsResponse(ErrorCode.Ok, "", result)
    }.recover {
      case ex: Exception =>
        logger.error("searchTripsByLocation Failure", ex)
        throw ex
    }
  }

  private def buildCreateTripResponse(futureResult: Future[Int]): Future[PublishTripResponse] = {
    futureResult.collect {
      case result if result > 0 =>
        PublishTripResponse(ErrorCode.Ok, "", Map("tripId" -> result))
      case result =>
        PublishTripResponse(ErrorCode.CreateTripFailed, "Create trip failed", Map("tripId" -> result))
    }.recover {
      case ex: Exception =>
        logger.error("createTrip failure", ex)
        throw ex;
    }
  }

  private def buildGetMyTripsResponse(futureResult: Future[Seq[TripInfo]]): Future[GetMyTripsResponse] = {
    futureResult.map { result =>
        GetMyTripsResponse(ErrorCode.Ok, "", result)
    }.recover {
      case ex: Exception =>
        logger.error("getTripDetailById failure", ex)
        throw ex;
    }
  }
}
