package domain.support

case class EntityNotFoundException(message: String, id: EntityIdentifier) extends Exception(message)

object EntityNotFoundException {

  def of(id: EntityIdentifier): EntityNotFoundException =
    EntityNotFoundException(s"""Entity not found(id=${id.toString}""", id)

}
