package pomeranian.models.login

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.models.login.LoginResultStatus.LoginResultStatus
import pomeranian.utils.CommonJsonProtocol

trait LoginResultInfoBase {
  def userId: Int;
  def username: String;
  def nickname: String;
  def avatar: Option[String];
}

trait LoginResultBase {
  def status: LoginResultStatus;
  def resultInfo: Option[AnyRef];
}

final case class MiniProgramLoginResultInfo(
                                             userId: Int,
                                             username: String,
                                             nickname: String,
                                             avatar: Option[String],
                                             openId: String,
                                           ) extends LoginResultInfoBase

final case class MiniProgramLoginResult(
                                       status: LoginResultStatus,
                                       resultInfo: Option[MiniProgramLoginResultInfo]
                                       ) extends LoginResultBase

trait LoginResultJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val miniProgramLoginResultInfoResponseFormat = jsonFormat5(MiniProgramLoginResultInfo)
  implicit val miniProgramLoginResultResponseFormat = jsonFormat2(MiniProgramLoginResult)
}
