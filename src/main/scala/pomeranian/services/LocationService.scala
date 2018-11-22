package pomeranian.services

import pomeranian.models.geo.CountryCities
import pomeranian.repositories.LocationRepository

import scala.concurrent.Future

trait LocationService {
  def getCountryCityCollection(): Future[Seq[CountryCities]]
}

class LocationServiceImpl extends LocationService {
  override def getCountryCityCollection(): Future[Seq[CountryCities]] = {
    LocationRepository.fetchCountriesCities()
  }
}