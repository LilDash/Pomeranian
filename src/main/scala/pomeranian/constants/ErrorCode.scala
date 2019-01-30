package pomeranian.constants

object ErrorCode {
  // 0 ~ 1999 for common error
  val Ok = 0
  val SaveFailed = 1001

  // 2000 ~ 2999 for user error
  val WeChatDecryptedUserInfoFailed = 2000
  val UserNotFound = 2001

  // 3000 ~ 3999 for trip error
  val TripNotFound = 3000
  val CreateTripFailed = 3001
  val ContactRequired = 3002
  val DeleteTripFailed = 3003
}
