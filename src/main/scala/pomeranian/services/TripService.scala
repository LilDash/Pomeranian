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

    if (departureCityId > 0 && arrivalCityId > 0){
      TripRepository.fetchTripsByDepartureCityAndArrivalCity(departureCityId, arrivalCityId, offset, num)
    } else if (departureCountryId > 0 && arrivalCityId > 0) {
      TripRepository.fetchTripsByDepartureCountryAndArrivalCity(departureCountryId, arrivalCityId, offset, num)
    } else if (departureCityId > 0 && arrivalCountryId > 0 ) {
      TripRepository.fetchTripsByDepartureCityAndArrivalCountry(departureCityId, arrivalCountryId, offset, num)
    } else if (departureCountryId > 0 && arrivalCountryId > 0) {
      TripRepository.fetchTripsByDepartureCountryAndArrivalCountry(departureCountryId, arrivalCountryId, offset, num)
    } else {
      throw new InvalidParameterException(s"Invalid parameters - " +
        s"departureCountryId: $departureCountryId " +
        s"departureCityId: $departureCityId " +
        s"arrivalCountryId: $arrivalCountryId " +
        s"arrivalCityId: $arrivalCityId")
    }
  }

}
