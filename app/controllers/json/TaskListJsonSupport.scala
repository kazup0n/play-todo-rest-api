package controllers.json

import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.EntityIdentifier
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class TaskListJson(id: Option[String], name: String, tasks: Seq[TaskJson], version: Option[Long])


object TaskListJsonSupport {

  private val reads: Reads[TaskListJson] = (
    (JsPath \ "id").read[String]
      and (JsPath \ "name").read[String]
      and (JsPath \ "version").read[Long]
    ).apply((id, name, version) => TaskListJson(Some(id), name, Seq.empty, Some(version)))

  private val writes: Writes[TaskListJson] = (
    (JsPath \ "id").write[String]
      and (JsPath \ "name").write[String]
      and (JsPath \ "tasks").lazyWrite(Writes.seq[TaskJson](TaskJsonSupport.writes))
      and (JsPath \ "version").write[Long]
    )
    .apply(unlift(TaskListJson.unapply).andThen(taskList =>
      (taskList._1.get,
        taskList._2,
        taskList._3,
        taskList._4.get)
    ))

  private val readsWithName: Reads[TaskListJson] = (
    (JsPath \ "name").read[String]).map { (name) => TaskListJson(None, name, Seq.empty, None) }
}

trait TaskListJsonSupport {

  protected def jsonToEntityWithName(json: JsValue): JsResult[TaskList] = {
    implicit val reads: Reads[TaskListJson] = TaskListJsonSupport.readsWithName
    Json.fromJson(json).map(taskListJson =>
      TaskList(
        id = TaskListIdentifier(EntityIdentifier.uuid),
        name = taskListJson.name,
        version = None
      ))
  }

  protected def jsonToEntity(json: JsValue): JsResult[TaskList] = {
    implicit val reads: Reads[TaskListJson] = TaskListJsonSupport.reads
    Json.fromJson(json).map { taskListJson =>
      TaskList(
        id = TaskListIdentifier(taskListJson.id.get),
        name = taskListJson.name,
        version = taskListJson.version
      )
    }
  }

  protected def entityToJson(taskList: TaskList): JsValue = {
    implicit val writes: Writes[TaskListJson] = TaskListJsonSupport.writes
    val json = TaskListJson(
      id = Some(taskList.id.toString),
      name = taskList.name,
      tasks = taskList.tasks.map(TaskJsonSupport.entityToTaskJson),
      version = taskList.version
    )
    Json.toJson(json)
  }

  protected def entitiesToJson(taskLists: Seq[TaskList]): JsValue = {
    implicit val writes: Writes[TaskListJson] = TaskListJsonSupport.writes
    val json = taskLists.map(taskList => TaskListJson(
      id = Some(taskList.id.toString),
      name = taskList.name,
      tasks = taskList.tasks.map(TaskJsonSupport.entityToTaskJson),
      version = taskList.version
    ))
    Json.toJson(json)
  }

}
