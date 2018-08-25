package pomeranian.models

final case class ValidationResult(
                                 isValid: Boolean,
                                 message: String = "",
                                 )

trait ObjectValidator {
  def validate(): ValidationResult
}
