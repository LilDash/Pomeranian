package pomeranian.models.login

import pomeranian.utils.EnumJsonConverter
import spray.json.{ DeserializationException, JsNumber, JsValue, RootJsonFormat }

object LoginResultStatus extends Enumeration {
  type LoginResultStatus = Value
  val Success, UserNotFound, CreateUserFailed, DecryptWeChatUserInfoFailed = Value

  implicit val enumConverter = new EnumJsonConverter(LoginResultStatus)

}

