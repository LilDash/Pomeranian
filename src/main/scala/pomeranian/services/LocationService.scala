package pomeranian.services

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory
import pomeranian.constants.ErrorCode
import pomeranian.models.geo.CountryCities
import pomeranian.models.responses.GetCountryCitiesResponse
import pomeranian.repositories.LocationRepository
import pomeranian.utils.measurement.Measurer

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LocationService {
  def getCountryCityCollection(): Future[GetCountryCitiesResponse]
}

class LocationServiceImpl(implicit system: ActorSystem, measurer: Measurer) extends LocationService {
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def getCountryCityCollection(): Future[GetCountryCitiesResponse] = {
    val futureResult = LocationRepository.fetchCountriesCities()
    measurer.measure("geo.getCountryCityCollection", futureResult)
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