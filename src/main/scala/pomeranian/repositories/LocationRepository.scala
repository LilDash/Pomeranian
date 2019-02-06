package pomeranian.repositories

import pomeranian.constants.Global
import pomeranian.models.geo.{ CityBrief, CityTableDef, CountryCities, CountryTableDef }
import pomeranian.utils.TimeUtil
import pomeranian.utils.database.MySqlDbConnection
import slick.lifted.{ Rep, TableQuery }
import slick.jdbc.MySQLProfile.api._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LocationRepository {
  def fetchCountriesCities(): Future[Seq[CountryCities]]
}

object LocationRepository extends LocationRepository {
  val db = MySqlDbConnection.db

  val country = TableQuery[CountryTableDef]
  val city = TableQuery[CityTableDef]

  override def fetchCountriesCities(): Future[Seq[CountryCities]] = {
    val countryCityResult: ListBuffer[CountryCities] = ListBuffer()

    val query = city.filter(_.recStatus === Global.DbRecActive)
      .join(country.filter(_.recStatus === Global.DbRecActive))
      .on(_.countryId === _.id)
      .result.map { rows =>
        rows.collect {
          case (rowCity, rowCountry) =>
            val cityBrief = CityBrief(rowCity.id, rowCity.name, rowCity.displayName)
            val countryInfo = countryCityResult.find(_.countryId == rowCity.countryId)
            if (!countryInfo.isDefined) {
              val newCountry = CountryCities(
                rowCity.countryId,
                rowCountry.name,
                rowCountry.displayName,
                cities = ListBuffer(cityBrief))
              countryCityResult += newCountry
            } else {
              countryInfo.get.cities += cityBrief
            }
        }
      }
    db.run(query).map { _ =>
      countryCityResult
    }
  }
}
