package pomeranian.services

import java.security.InvalidParameterException

import pomeranian.models.trip.TripInfo
import pomeranian.repositories.TripRepository

import scala.concurrent.Future

trait TripService {
  def searchTripsByLocation(
                             departureCountryId: Int,
                             departureCityId: Int,
                             arrivalCountryId: Int,
                             arrivalCityId: Int,
                             offset: Int,
                             num: Int): Future[Any]
}

class TripServiceImpl extends  TripService {
  override def searchTripsByLocation(
                                      departureCountryId: Int,
                                      departureCityId: Int,
                                      arrivalCountryId: Int,
                                      arrivalCityId: Int,
                                      offset: Int,
                                      num: Int): Future[Seq[TripInfo]] ={

    (departureCountryId, departureCityId, arrivalCountryId, arrivalCityId) match {
      case (0, 0, 0, 0) => TripRepository.fetchAllTrips(offset, num)
      case (_, depCityId, _, arrCityId) if depCityId > 0 && arrCityId > 0 =>
        TripRepository.fetchTripsByDepartureCityAndArrivalCity(depCityId, arrCityId, offset, num)
      case (_, depCityId, 0, 0) if depCityId > 0 =>
        TripRepository.fetchTripsByDepartureCity(depCityId, offset, num)
      case (0, 0, _, arrCityId) if arrCityId > 0 =>
        TripRepository.fetchTripsByArrivalCity(arrCityId, offset, num)
      case (depCountryId, 0, _, arrCityId) if depCountryId > 0 && arrCityId > 0 =>
        TripRepository.fetchTripsByDepartureCountryAndArrivalCity(depCountryId, arrCityId, offset, num)
      case (_, depCityId, arrCountryId, 0) if depCityId > 0 && arrCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCityAndArrivalCountry(depCityId, arrCountryId, offset, num)
      case (depCountryId, 0, arrCountryId, 0) if depCountryId > 0 && arrCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCountryAndArrivalCountry(depCountryId, arrCountryId, offset, num)
      case (depCountryId, 0, 0, 0) if depCountryId > 0 =>
        TripRepository.fetchTripsByDepartureCountry(depCountryId, offset, num)
      case (0, 0, arrCountryId, 0) if arrCountryId > 0 =>
        TripRepository.fetchTripsByArrivalCountry(arrCountryId, offset, num)
      case _ =>
        throw new InvalidParameterException(s"Invalid parameters - " +
          s"departureCountryId: $departureCountryId " +
          s"departureCityId: $departureCityId " +
          s"arrivalCountryId: $arrivalCountryId " +
          s"arrivalCityId: $arrivalCityId")
    }
  }

}
