package pomeranian.constants

object ErrorCode {
  val Ok = 0

  val SaveFailed = 1001

  // 2000 ~ 2999 for user error

  // 3000 ~ 3999 for trip error
  val TripNotFound = 3000
}
