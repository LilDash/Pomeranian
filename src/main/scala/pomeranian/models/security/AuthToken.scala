package pomeranian.models.security

case class AuthToken(
                    token: String,
                    expirationTime: Long, // In seconds
                    )
