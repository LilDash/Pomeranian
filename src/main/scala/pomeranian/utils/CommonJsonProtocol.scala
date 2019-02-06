package pomeranian.utils

import java.sql.Timestamp

import pomeranian.models.login.LoginResultStatus
import pomeranian.models.login.LoginResultStatus.LoginResultStatus
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

  implicit def listBufferFormat[T: JsonFormat] = new RootJsonFormat[ListBuffer[T]] {
    def write(listBuffer: ListBuffer[T]) = JsArray(listBuffer.map(_.toJson).toVector)
    def read(value: JsValue): ListBuffer[T] = value match {
      case JsArray(elements) => elements.map(_.convertTo[T])(collection.breakOut)
      case x => deserializationError("Expected ListBuffer as JsArray, but got " + x)
    }
  }

  //  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] =
  //    new RootJsonFormat[T#Value] {
  //      def write(obj: T#Value): JsValue = JsNumber(obj.id)
  //      def read(json: JsValue): T#Value = {
  //        json match {
  //          case JsNumber(number) => enu(number.toInt)
  //          case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
  //        }
  //      }
  //    }

}

class EnumJsonConverter[T <: Enumeration](enu: T) extends RootJsonFormat[T#Value] {
  override def write(obj: T#Value): JsValue = JsNumber(obj.id)
  override def read(json: JsValue): T#Value = {
    json match {
      case JsNumber(number) => enu(number.toInt)
      case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
    }
  }
}
