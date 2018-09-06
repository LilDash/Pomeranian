package pomeranian.models.security

trait Role {
  def name: String
  def acl: Seq[String]
}

object Role {

  case object Basic extends Role {
    override def name: String = "basic"

    override def acl: Seq[String] = Seq("")
  }

  case object Admin extends Role {
    override def name: String = "admin"

    override def acl: Seq[String] = Seq("")
  }

}