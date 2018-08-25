package pomeranian.models

object UploadStorage {
  val Oss = "oss"
  val Local = "local"
}

final case class UploadNotification(
                                   storage: String,
                                   mimeType: String,
                                   key: String,
                                   title: String,
                                   size: Int,
                                   metadata: String,
                                   ) extends ObjectValidator {
  override def validate(): ValidationResult = {
    if (storage.trim.isEmpty || key.trim.isEmpty) {
      ValidationResult(false, "Invalid Parameters")
    } else if (storage != UploadStorage.Oss && storage != UploadStorage.Local) {
      ValidationResult(false, "Unsupported upload storage")
    } else {
      ValidationResult(true)
    }
  }
}
