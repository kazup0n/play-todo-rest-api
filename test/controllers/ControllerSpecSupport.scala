package controllers

import domain.lifecycle.tasklist.TaskListRepository
import domain.model.tasklist.TaskList
import domain.support.OnMemoryIOContext
import infra.IOContextProvider
import play.api.libs.json._

trait ControllerSpecSupport {

  def buildTaskListController(taskLists: TaskList*) = new TaskListController(buildRepository(taskLists: _*), contextProvider)

  def contextProvider: IOContextProvider = OnMemoryIOContext

  def buildRepository(taskLists: TaskList*) = TaskListRepository.onMemory(Map(taskLists.map { taskList => taskList.id -> taskList }: _*))

  implicit val ctx = contextProvider.getContext

  def toJson(ts: TaskList) = taskList(
    ts.id.toString,
    ts.name,
    ts.version.get,
    ts.tasks.map { t => task(t.id.value, t.title, t.description) }: _*
  )

  def taskList(id: String, name: String, version: Long, tasks: JsValue*): JsValue = JsObject(Seq(
    "id" -> JsString(id),
    "name" -> JsString(name),
    "version" -> JsNumber(version),
    "tasks" -> JsArray(tasks),
    "version" -> JsNumber(1L)
  ))

  def task(id: String, title: String, description:String): JsValue = JsObject(Seq(
    "id" -> JsString(id),
    "title" -> JsString(title),
    "description" -> JsString(description),
    "version" -> JsNumber(1L)
  ))

}
