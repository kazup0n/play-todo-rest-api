package infra.jdbc.record

import infra.jdbc.CRUDMapper
import scalikejdbc.WrappedResultSet
import skinny.orm.Alias

case class TaskRecord(id: String, tasklistId: String, title: String, description:String, version: Long)

object TaskRecord extends CRUDMapper[TaskRecord] {

  override def tableName: String = "tasks"

  override def defaultAlias: Alias[TaskRecord] = createAlias("tasks")


  override def toAttributes(record: TaskRecord): Seq[(Symbol, Any)] = {
    println("id " + record.id)
    println("tasklistId " + record.tasklistId)
    Seq(
    'id -> record.id,
    'tasklist_id -> record.tasklistId,
    'title -> record.title,
    'description -> record.description,
    'version -> record.version
  )}


  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[TaskRecord]): TaskRecord = {
    TaskRecord(
      rs.get(n.field("id")),
      rs.get(n.field("tasklist_id")),
      rs.get(n.field("title")),
      rs.get(n.field("description")),
      rs.get(n.field("version"))
    )
  }
}