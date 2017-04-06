package infra.jdbc.record

import infra.jdbc.CRUDMapper
import scalikejdbc.{WrappedResultSet, _}
import skinny.orm.Alias

case class TaskListRecord(id: String, name: String, version: Long, tasks: Seq[TaskRecord] = Nil)

object TaskListRecord extends CRUDMapper[TaskListRecord] {

  val taskRef = hasMany[TaskRecord](
    many = TaskRecord -> TaskRecord.defaultAlias,
    on = (taskList, task) => sqls.eq(taskList.id, task.tasklistId),
    merge = (taskList, tasks) => taskList.copy(tasks = tasks)
  )

  override def tableName: String = "task_lists"

  override def defaultAlias: Alias[TaskListRecord] = createAlias("task_lists")

  override def toAttributes(record: TaskListRecord): Seq[(Symbol, Any)] = Seq(
    'id -> record.id,
    'name -> record.name,
    'version -> record.version)

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[TaskListRecord]): TaskListRecord =
    TaskListRecord(
      rs.get("id"),
      rs.get("name"),
      rs.get("version"))

}
