package domain.support

import infra.{IOContext, IOContextProvider}
import scalikejdbc.{AutoSession, DBSession}

case class JdbcIOContext(session: DBSession) extends IOContext

class JdbcIOContextProvider extends IOContextProvider {

  override def getContext: IOContext = JdbcIOContext(AutoSession)

}


trait JdbcRepository[ID <: EntityIdentifier, E <: Entity[ID]] extends SimpleRepository[ID, E] {

  protected def withDB[T](ctx: IOContext)(f: DBSession => T): T = {
    ctx match {
      case JdbcIOContext(session) => f(session)
      case _ => throw new IllegalStateException("Unsupported context(JdbcIOContext is needed)")
    }
  }

}
