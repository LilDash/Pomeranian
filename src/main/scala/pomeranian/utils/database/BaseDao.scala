package pomeranian.utils.database

import java.sql.{ Connection, PreparedStatement, ResultSet, Statement }

abstract class BaseDao[T](conn: Connection) {

  protected def insert[T](sql: String, params: Array[Any])(resultConverter: ResultSet => T) = {
    val statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    setParameters(statement, params)
    statement.executeUpdate()
    val result = statement.getGeneratedKeys
    result.next()
    resultConverter(result)
  }

  private def setParameters(statement: PreparedStatement, params: Array[Any]): Unit = {
    for (i <- 1 to params.length) {
      statement.setObject(i, params(i - 1))
    }
  }
}
