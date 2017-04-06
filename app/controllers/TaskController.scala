package controllers

import javax.inject.Inject

import controllers.json.TaskJsonSupport
import domain.lifecycle.tasklist.TaskListRepository
import domain.model.task.TaskIdentifier
import domain.model.tasklist.TaskListIdentifier
import domain.support.EntityNotFoundException
import infra.{IOContext, IOContextProvider}
import play.api.mvc.Action

import scala.util.{Failure, Success}
import play.api.Logger

class TaskController @Inject()(taskListRepository: TaskListRepository, contextProvider: IOContextProvider)
  extends ControllerSupport with TaskJsonSupport {

  implicit val ioContext: IOContext = contextProvider.getContext

  def create(taskListId: String) = Action { request =>
    val taskListEntityId = TaskListIdentifier(taskListId)
    jsonToEntityWithoutId(request.body.asJson.get).map { task =>
      taskListRepository.storeTask(taskListEntityId, task)
        .map(_._2)
        .map(newTask => Created(entityToJson(newTask)))
        .recover {
          case e: EntityNotFoundException => entityNotFound(e.id)
          case t:Throwable => {
            Logger.error(t.getMessage)
            InternalServerError
          }
        }.getOrElse(InternalServerError)
    }.recover {
      case jsError => BadRequest(validationError(jsError.errors))
    }.getOrElse(InternalServerError)
  }

  def delete(taskListId: String, taskId: String) = Action {
    taskListRepository.deleteTask(TaskListIdentifier(taskListId), TaskIdentifier(taskId)) match {
      case Success(_) => NoContent
      case Failure(e: EntityNotFoundException) => entityNotFound(e.id)
      case t:Throwable => {
        Logger.error(t.getMessage)
        InternalServerError
      }
    }
  }

}
