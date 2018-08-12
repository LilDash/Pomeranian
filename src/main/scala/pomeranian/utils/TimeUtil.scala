package pomeranian.utils

import java.sql.Timestamp
import java.util.Calendar

object TimeUtil {
  def timeStamp(): Timestamp = {
    new Timestamp(Calendar.getInstance.getTime.getTime)
  }
}
