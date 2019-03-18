package pomeranian.http

import java.security.InvalidParameterException

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import scala.reflect.ClassTag

object Directives extends RequestContextDirectives

trait RequestContextDirectives {
  def handle[T: ClassTag](handler: T ⇒ ToResponseMarshallable)(implicit um: FromRequestUnmarshaller[T]): Route = {
    entity(as[T]) { requestObj =>
      try {
        complete(handler(requestObj))
      } catch {
        case _: InvalidParameterException =>
          complete(StatusCodes.BadRequest)
        case _: IllegalArgumentException =>
          complete(StatusCodes.BadRequest)
        case _: Throwable =>
          complete(StatusCodes.InternalServerError)
      }
    }
  }
}