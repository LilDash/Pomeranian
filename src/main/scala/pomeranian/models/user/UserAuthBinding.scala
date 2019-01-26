package pomeranian.models.user

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class UserAuthBinding(
                                id: Int,
                                userId: Int,
                                authTypeId: Int,
                                username: String,
                                recStatus: Int,
                                recCreatedWhen: Timestamp,
                                recUpdatedWhen: Timestamp,
                                )