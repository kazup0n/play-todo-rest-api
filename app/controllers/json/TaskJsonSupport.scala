package controllers.json

import domain.model.task.{Task, TaskIdentifier}
import domain.support.EntityIdentifier
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads, Writes, _}

case class TaskJson(id: Option[String], title: String, description:String, version: Option[Long])

object TaskJsonSupport {

  private[json] val writes: Writes[TaskJson] = (
    (JsPath \ "id").write[String]
      and (JsPath \ "title").write[String]
      and (JsPath \ "description").write[String]
      and (JsPath \ "version").write[Long])
    .apply(unlift(TaskJson.unapply).andThen(task => (
      task._1.get.toString,
      task._2.toString,
      task._3,
      task._4.get)))


  private[json] val readsWithoutId: Reads[TaskJson] =
    ((JsPath \ "title").read[String]
      and (JsPath \ "description").read[String]
      ).apply((title, description)=> TaskJson(None, title, description, None))

  private[json] def entityToTaskJson(task: Task) = TaskJson(
    Some(task.id.toString),
    task.title,
    task.description,
    task.version)

}

trait TaskJsonSupport {

  protected def jsonToEntityWithoutId(json: JsValue): JsResult[Task] = {
    TaskJsonSupport.readsWithoutId.reads(json).map { taskJson =>
      Task(TaskIdentifier(EntityIdentifier.uuid), taskJson.title, taskJson.description, None)
    }
  }

  protected def entityToJson(task: Task): JsValue =
    TaskJsonSupport.writes.writes(TaskJsonSupport.entityToTaskJson(task))

}
