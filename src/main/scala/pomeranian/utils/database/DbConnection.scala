package pomeranian.utils.database

import slick.jdbc.JdbcProfile

trait DbConnection {
  val driver: JdbcProfile
  val db: driver.api.Database
}