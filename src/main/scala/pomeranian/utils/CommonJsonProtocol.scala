package pomeranian.utils

import java.sql.Timestamp

import spray.json.{ DefaultJsonProtocol, DeserializationException, JsNumber, JsValue, RootJsonFormat }

trait CommonJsonProtocol extends DefaultJsonProtocol {

  implicit object TimestampJsonFormat extends RootJsonFormat[Timestamp] {
    def write(timestamp: Timestamp) = JsNumber(timestamp.getTime())
    def read(json: JsValue) = json match {
      case JsNumber(time) => new Timestamp(time.toLong)
      case _ => throw new DeserializationException("Number expected")
    }
  }
}
