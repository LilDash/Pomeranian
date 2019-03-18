package pomeranian.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import org.slf4j.LoggerFactory
import pomeranian.models.responses.GeoResponseJsonProtocol
import pomeranian.services.BotService
import pomeranian.utils.measurement.Measurer

class BotRoute(botService: BotService)(implicit system: ActorSystem, measurer: Measurer)
  extends BaseRoute with GeoResponseJsonProtocol {

  lazy val logger = LoggerFactory.getLogger(this.getClass)

  val route: Route = {
    pathPrefix("bot") {
      path("init") {
        pathEnd {
          get {
            botService.initBot("/home/dash/projects/pinggage/bot.csv")
            complete("")
          }
        }
      } ~ path("trips") {
        pathEnd {
          get {
            botService.initTrips("/home/dash/projects/pinggage/flights.csv")
            complete("")
          }
        }
      }
    }
  }
}
