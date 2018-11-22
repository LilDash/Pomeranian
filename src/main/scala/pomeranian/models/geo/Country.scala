package pomeranian.models.geo

import java.sql.Timestamp

import akka.http.scaladsl.model.DateTime

final case class Country (
  id: Int,
  name: String,
  displayName: String,
  recStatus: Int,
  recCreatedWhen: Timestamp,
  recUpdatedWhen: Timestamp,
)
