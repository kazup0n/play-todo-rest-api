package infra.jdbc

import domain.model.tasklist.TaskListIdentifier
import domain.support.EntityNotFoundException
import infra.jdbc.record.TaskListRecord
import scalikejdbc.{DBSession, sqls}

import scala.util.Try

object TaskListDao extends DaoSupport[TaskListRecord] {

  override val mapper: CRUDMapper[TaskListRecord] = TaskListRecord

  override def findAll()(implicit session: DBSession): Try[Seq[TaskListRecord]] = Try{
    mapper.joins(TaskListRecord.taskRef).findAll()
  }

  override def findById(id: String)(implicit session: DBSession): Try[TaskListRecord] =
    Try {
      mapper
        .joins(TaskListRecord.taskRef)
        .findBy(sqls.eq(mapper.primaryKeyField, id)
        ).getOrElse(throw EntityNotFoundException.of(TaskListIdentifier(id)))
    }


}
