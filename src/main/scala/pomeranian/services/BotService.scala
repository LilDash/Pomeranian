package pomeranian.services

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.joda.time.DateTime
import pomeranian.constants.{AuthType, Global}
import pomeranian.models.trip.Trip
import pomeranian.models.user.{User, UserInfo}
import pomeranian.repositories.{TripRepository, UserRepository}
import pomeranian.utils.TimeUtil

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BotService {
  def initBot(filename: String): Future[Int]
  def initTrips(filename: String): Future[Int]
  def buildBotAvatarUrl(avatar: String): String
}

class BotServiceImpl(userService: UserService) extends BotService {

  override def initBot(filename: String): Future[Int] = {
    val resource = scala.io.Source.fromFile(filename)
    try {
      val time = TimeUtil.timeStamp()
      for (line <- resource.getLines) {
        val row = line.split(",").map(_.trim)
        val user = User(0, row(0), row(1), 0, 0,
          Option(row(2)), true, Global.DbRecActive, time, time)
        UserRepository.insertUserWithBinding(AuthType.WeChatMiniProgram, row(0), user)
      }
    } finally {
      resource.close()
    }

    Future.successful(1)
  }

  override def initTrips(filename: String): Future[Int] = {
    val resource = scala.io.Source.fromFile(filename)
    try {
      val now = TimeUtil.timeStamp()
      for (line <- resource.getLines()){
        val row = line.split(",").map(_.trim)
        userService.findUserByAuthType(AuthType.WeChatMiniProgram, row(0)).map { userInfo =>
          val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
          val parsedDateDep = dateFormat.parse(row(7))
          val parsedDateArr = dateFormat.parse(row(8))
          val u = userInfo.get
          val memo = if (row.size > 11) {
            row(11)
          } else {
            ""
          }
            val trip = Trip(
              0,
              u.id,
              row(1).toInt,
              row(2).toInt,
              row(3),
              row(4).toInt,
              row(5).toInt,
              row(6).toInt,
              "CNY", // TODO: by default
              new Timestamp(parsedDateDep.getTime),
              new Timestamp(parsedDateArr.getTime),
              row(9).toInt,
              row(10),
              Option(memo),
              Global.DbRecActive,
              now,
              now,
            )
            TripRepository.insert(trip)
        }
      }
    } finally {
      resource.close()
    }

    Future.successful(1)
  }

  override def buildBotAvatarUrl(avatar: String): String = {
    s"https://pinggage.0pla.net/image/${avatar}"
  }
}
