package pomeranian.utils.database

import java.sql.{ PreparedStatement, ResultSet, SQLException, Statement }

import akka.actor.ActorSystem
import akka.event.Logging

abstract class BaseDao() {
  //implicit def system: ActorSystem
  //lazy protected val log = Logging(context.system, this.getClass)

  protected def insert(table: String, params: Map[String, Any]): Int = {
    var ret = 0
    val conn = DBConnectionPool.getConnection()
    try {
      val columns = params.keys.mkString("`,`")
      val paramsPlaceholder = List.fill(params.size)("?").mkString(",")
      val insertSql =
        s"""
           |INSERT INTO $table (`$columns`)
           |VALUES($paramsPlaceholder);""".stripMargin
      val statement = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
      setParameters(statement, params.values.toArray)
      statement.executeUpdate()
      val result = statement.getGeneratedKeys
      result.next()
      ret = result.getInt(1)
      statement.close()
      result.close()
      ret
    } catch {
      case sqlEx: SQLException => {
        //log.error("Database insert sql exception: {}", sqlEx.getMessage)
        ret
      }
      case ex: Exception => {
        //log.error("Database insert exception: {}", ex.getMessage)
        ret
      }
    } finally {
      DBConnectionPool.releaseConnection(conn)
    }
  }

  private def setParameters(statement: PreparedStatement, params: Array[Any]): Unit = {
    for (i <- 1 to params.length) {
      statement.setObject(i, params(i - 1))
    }
  }
}
