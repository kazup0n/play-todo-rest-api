package infra.jdbc

import infra.jdbc.record.TaskRecord
import scalikejdbc.{DBSession, sqls}

import scala.util.Try

object TaskDao extends DaoSupport[TaskRecord] {

  override val mapper: CRUDMapper[TaskRecord] = TaskRecord


  def deleteByTaskListId(taskListId: String)(implicit session: DBSession): Try[Boolean] =
    Try {
      mapper.deleteBy(sqls.eq(mapper.column.field("tasklist_id"), taskListId)) > 0L
    }

}
