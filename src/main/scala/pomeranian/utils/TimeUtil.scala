package pomeranian.utils

import java.sql.Timestamp

object TimeUtil {
  def timeStamp(): Timestamp = {
    new Timestamp(System.currentTimeMillis)
  }

  def now(): Long = {
    System.currentTimeMillis
  }
}
