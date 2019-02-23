package pomeranian.services

import org.slf4j.LoggerFactory
import pomeranian.constants.ErrorCode
import pomeranian.models.geo.CountryCities
import pomeranian.models.responses.GetCountryCitiesResponse
import pomeranian.repositories.LocationRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LocationService {
  def getCountryCityCollection(): Future[GetCountryCitiesResponse]
}

class LocationServiceImpl extends LocationService {
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def getCountryCityCollection(): Future[GetCountryCitiesResponse] = {
    val futureResult = LocationRepository.fetchCountriesCities()
    buildGetCountryCitiesResponse(futureResult)
  }

  private def buildGetCountryCitiesResponse(futureResult: Future[Seq[CountryCities]]): Future[GetCountryCitiesResponse] = {
    futureResult.map { result =>
      GetCountryCitiesResponse(ErrorCode.Ok, "", result)
    }.recover {
      case ex: Exception =>
        logger.error("getCountryCityCollection failure", ex)
        throw ex
    }
  }
}