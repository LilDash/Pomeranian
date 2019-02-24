package pomeranian.utils.measurement

import com.paulgoldbaum.influxdbclient.Parameter.Precision
import com.paulgoldbaum.influxdbclient.{InfluxDB, Point}
import org.slf4j.LoggerFactory
import pomeranian.utils.{AppConfiguration, TimeUtil}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Measurer {
  type MetricTags = Map[String, String]
  def send(metricName: String, value: Any, tags: MetricTags): Unit
  def measure[T](metricName: String, func: () => T, tags: MetricTags): T
  def measure[T](metricName: String, func: MetricTags => T, tags: MetricTags): T
  def measure[T](metricName: String, future: Future[T]): Future[T]
  def measure[T](metricName: String, tags: MetricTags, future: Future[T]): Future[T]
}

class MeasurerImpl extends Measurer {
  val valueFieldKey = "value"
  val metricNamePrefix = "pinggage."
  val influxDb = InfluxDB.connect(AppConfiguration.influxDbHost, AppConfiguration.influxDbPort) // TODO: username/password
  val database = influxDb.selectDatabase(AppConfiguration.influxDbName)
  lazy val logger = LoggerFactory.getLogger(this.getClass)


//  def closeConnection() = {
//    database.close()
//  }

  override def send(metricName: String, value: Any, tags: MetricTags): Unit = {
    var point = Point(s"${metricNamePrefix}${metricName}".toLowerCase, TimeUtil.now())
    point = value match {
      case stringValue: String => point.addField(valueFieldKey, stringValue)
      case longValue: Long => point.addField(valueFieldKey, longValue)
      case doubleValue: Double => point.addField(valueFieldKey, doubleValue)
      case booleanValue: Boolean => point.addField(valueFieldKey, booleanValue)
      case bigDecimalValue: BigDecimal => point.addField(valueFieldKey, bigDecimalValue)
      case _ => throw new IllegalArgumentException(s"Unsupported value type: ${value}")
    }
    for ((k, v) <- tags) point.addTag(k, v)
    database.write(point,
      precision = Precision.MILLISECONDS).recover {
      case ex: Exception =>
        logger.error("Measurer send metric failed", ex)
    }
  }

  override def measure[T](metricName: String, func: () => T, tags: MetricTags): T = {
    val start = TimeUtil.now()
    val ret: T = func()
    val elapse = TimeUtil.now() - start
    Future { send(metricName, elapse, tags) }
    ret
  }

  override def measure[T](metricName: String, func: MetricTags => T, tags: MetricTags): T = {
    val start = TimeUtil.now()
    val ret: T = func(tags)
    val elapse = TimeUtil.now() - start
    Future { send(metricName, elapse, tags) }
    ret
  }

  override def measure[T](metricName: String, future: Future[T]): Future[T] = {
    measure(metricName, Map[String, String](), future)
  }

  override def measure[T](metricName: String, tags: MetricTags, future: Future[T]): Future[T] = {
    val start = TimeUtil.now()
    future.onComplete {
      case _ =>
        val elapse = TimeUtil.now() - start
        Future { send(metricName, elapse, tags) }
    }
    future
  }

}
