package domain.support

trait ValueObject {

  override def equals(obj: scala.Any): Boolean

  override def hashCode(): Int

}
