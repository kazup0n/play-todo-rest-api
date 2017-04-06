package domain.support

trait Entity[ID <: EntityIdentifier] {

  val id: ID
  val version: Option[Long]

  override final def hashCode: Int = id.hashCode

  override final def equals(that: Any): Boolean = that match {
    case that: Entity[ID] => id.equals(that.id)
    case _ => false
  }

  def withVersion(version: Long): Entity[ID]


}
