package domain.support

import infra.IOContext

import scala.util.Try

trait SimpleRepository[ID <: EntityIdentifier, E <: Entity[ID]] {

  type This <: SimpleRepository[ID, E]

  def findById(id: ID)(implicit ctx: IOContext): Try[E]

  def exists(id: ID)(implicit ctx: IOContext): Try[Boolean]

  def findAll()(implicit ctx: IOContext): Try[Seq[E]]

  def store(entity: E)(implicit ctx: IOContext): Try[(This, E)]

  def deleteById(id: ID)(implicit ctx: IOContext): Try[This]

}
