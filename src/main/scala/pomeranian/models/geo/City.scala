package pomeranian.models.geo

import java.sql.Timestamp

final case class City (
  id: Int,
  name: String,
  displayName: String,
  countryId: Int,
  recStatus: Int,
  recCreatedWhen: Timestamp,
  recUpdatedWhen: Timestamp,
)
