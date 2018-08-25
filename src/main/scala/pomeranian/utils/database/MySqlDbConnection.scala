package pomeranian.utils.database

import pomeranian.utils.AppConfiguration

object MySqlDbConnection extends DbConnection {
  override val driver = slick.jdbc.MySQLProfile

  import driver.api._

  override lazy val db = Database.forConfig("", AppConfiguration.dbMysqlConfig)
}
