package domain.support

import infra.{IOContext, IOContextProvider}

import scala.util.Try

object OnMemoryIOContext extends IOContext with IOContextProvider {

  override def getContext: IOContext = OnMemoryIOContext
}

abstract class OnMemorySimpleRepository[ID <: EntityIdentifier, E <: Entity[ID]](entities: Map[ID, E])
  extends SimpleRepository[ID, E] {

  override def findById(id: ID)(implicit ctx: IOContext): Try[E] = Try {
    entities.get(id) match {
      case Some(e) => e
      case None => throw EntityNotFoundException.of(id)
    }
  }

  override def exists(id: ID)(implicit ctx: IOContext): Try[Boolean] = Try {
    entities.contains(id)
  }

  override def findAll()(implicit ctx: IOContext): Try[Seq[E]] = Try {
    entities.values.toSeq
  }

  override def store(entity: E)(implicit ctx: IOContext): Try[(This, E)] = Try {
    val next = entity.withVersion(entity.version.getOrElse(0L) + 1L).asInstanceOf[E]
    if (entity.version.isDefined) {
      (createInstance(entities.updated(entity.id, next)), next)
    } else {
      (createInstance(entities + (entity.id -> next)), next)
    }
  }

  override def deleteById(id: ID)(implicit ctx: IOContext): Try[This] = Try {
    createInstance(entities - id)
  }

  protected def createInstance(entitiesMap: Map[ID, E]): This
}
