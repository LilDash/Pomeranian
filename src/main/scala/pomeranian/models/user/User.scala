package pomeranian.models.user

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class User (
                        id: Int,
                        username: String,
                        nickname: String,
                        rating: Int,
                        tripsNum: Int,
                        avatar: Option[String],
                        recStatus: Int,
                        recCreatedWhen: Timestamp,
                        recUpdatedWhen: Timestamp,
                      )

final case class UserInfo (
                            id: Int,
                            username: String,
                            nickname: String,
                            rating: Int,
                            tripsNum: Int,
                            avatar: Option[String],
                          )

trait UserJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val userInfoFormat = jsonFormat6(UserInfo)
}