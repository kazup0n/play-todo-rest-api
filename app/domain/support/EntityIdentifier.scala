package domain.support

import java.util.UUID

trait EntityIdentifier {

  def value: String

  override def hashCode(): Int = value.hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: EntityIdentifier => that.value.equals(value)
    case _ => false
  }

  override def toString: String = value

}

object EntityIdentifier {

  def uuid: String = UUID.randomUUID().toString

}
