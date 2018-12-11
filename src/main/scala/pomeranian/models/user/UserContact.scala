package pomeranian.models.user

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pomeranian.utils.CommonJsonProtocol

final case class ContactType (
                             id: Int,
                             name: String,
                             displayName: String,
                             recStatus: Int,
                             recCreatedWhen: Timestamp,
                             recUpdatedWhen: Timestamp,
                             )

final case class UserContact (
                             id: Int,
                             userId: Int,
                             contactTypeId: Int,
                             contactTypeValue: String,
                             recStatus: Int,
                             recCreatedWhen: Timestamp,
                             recUpdatedWhen: Timestamp,
                             )

final case class UserContactInfo (
                                  id: Int,
                                  contactTypeId: Int,
                                  contactTypeName: String,
                                  contactTypeDisplayName: String,
                                  contactTypeValue: String,
                                  )

trait UserContactJsonProtocol extends SprayJsonSupport with CommonJsonProtocol {
  implicit val contactTypeFormat = jsonFormat6(ContactType)
  implicit val userContactFormat = jsonFormat7(UserContact)
  implicit val userContactInfoFormat = jsonFormat5(UserContactInfo)
}