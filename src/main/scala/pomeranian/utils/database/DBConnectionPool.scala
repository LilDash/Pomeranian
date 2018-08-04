package pomeranian.utils.database

import java.sql.{ Connection, DriverManager }

import pomeranian.utils.AppConfiguration

import scala.collection.mutable.ListBuffer

class PooledConnection(val connection: Connection, var isBusy: Boolean = false)

object DBConnectionPool {
  private val maxConnections = AppConfiguration.mysqlMaxConnections
  private val initialConnections = AppConfiguration.mysqlInitialConnections
  private val incrementalConnections = AppConfiguration.mysqlIncrementalConnections
  private val driver = AppConfiguration.mysqlDriver
  private val url = AppConfiguration.mysqlUrl
  private val username = AppConfiguration.mysqlUsername
  private val password = AppConfiguration.mysqlPassword
  private val sleepTime = AppConfiguration.mysqlGetConnectionSleepTime

  private var connectionNum = 0
  private var pool = new ListBuffer[PooledConnection]()

  private def createConnections(num: Int) = {
    val n = num.min(maxConnections - connectionNum)
    for (_ <- 1 to n) {
      val connection = DriverManager.getConnection(url, username, password)
      val pooledConnection = new PooledConnection(connection)
      pool += pooledConnection
      connectionNum += 1
    }
  }

  private def findFreeConnection(): Option[Connection] = {
    val pooledConn = pool.find(p => !p.isBusy)
    if (pooledConn.isDefined) {
      pooledConn.get.isBusy = true
      Option(pooledConn.get.connection)
    } else {
      None
    }
  }

  private def getFreeConnnection(): Option[Connection] = {
    val conn = findFreeConnection()
    if (!conn.isDefined) {
      createConnections(incrementalConnections)
      findFreeConnection()
    } else {
      conn
    }
  }

  private def waitForAWhile() {
    println("Connection is busy")
    Thread.sleep(sleepTime)
  }

  def getConnection(): Connection = {
    AnyRef.synchronized({
      var conn = getFreeConnnection()
      while (!conn.isDefined) {
        waitForAWhile()
        conn = getFreeConnnection()
      }
      conn.get
    })
  }

  def releaseConnection(con: Connection) {
    val pooledConn = pool.find(p => p.connection == con)
    if (pooledConn.isDefined) {
      pooledConn.get.isBusy = false;
    }
  }

  // Initialize connection pool
  Class.forName(driver)
  createConnections(initialConnections)
}