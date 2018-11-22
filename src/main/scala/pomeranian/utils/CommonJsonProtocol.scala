package pomeranian.utils

import java.sql.Timestamp
import spray.json._
import scala.collection.mutable.ListBuffer

trait CommonJsonProtocol extends DefaultJsonProtocol {

  implicit object TimestampJsonFormat extends RootJsonFormat[Timestamp] {
    def write(timestamp: Timestamp) = JsNumber(timestamp.getTime())
    def read(json: JsValue) = json match {
      case JsNumber(time) => new Timestamp(time.toLong)
      case _ => throw new DeserializationException("Number expected")
    }
  }

  implicit def listBufferFormat[T :JsonFormat] = new RootJsonFormat[ListBuffer[T]] {
    def write(listBuffer: ListBuffer[T]) = JsArray(listBuffer.map(_.toJson).toVector)
    def read(value: JsValue): ListBuffer[T] = value match {
      case JsArray(elements) => elements.map(_.convertTo[T])(collection.breakOut)
      case x => deserializationError("Expected ListBuffer as JsArray, but got " + x)
    }
  }

}
