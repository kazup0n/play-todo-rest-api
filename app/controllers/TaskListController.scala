package controllers

import javax.inject.Inject

import controllers.json.TaskListJsonSupport
import domain.lifecycle.tasklist.TaskListRepository
import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.EntityNotFoundException
import infra.{IOContext, IOContextProvider}
import play.api.libs.json._
import play.api.mvc.Action

import scala.util.{Failure, Success}

class TaskListController @Inject()(taskListRepository: TaskListRepository, contextProvider: IOContextProvider) extends ControllerSupport
  with TaskListJsonSupport {

  implicit val ioContext: IOContext = contextProvider.getContext

  def findAll = Action {
    val json = entitiesToJson(taskListRepository.findAll.get)
    Ok(Json.toJson(json))
  }

  def findById(id:String) = Action {
    taskListRepository
      .findById(TaskListIdentifier(id)) match {
      case Success(taskList) => Ok(entityToJson(taskList))
      case Failure(e:EntityNotFoundException) => entityNotFound(e.id)
      case Failure(e) => InternalServerError
    }

  }

  def create = Action { request =>
    jsonToEntityWithName(request.body.asJson.get).map { entity =>
      taskListRepository.store(entity).map {
        case (_, taskList: TaskList) => entityToJson(taskList)
      }.get
    }.fold(errors => BadRequest(validationError(errors)), result => Created(result))
  }

  def update(id: String) = Action { request =>
    taskListRepository.exists(TaskListIdentifier(id)).map { exists =>
      if (exists) {
        jsonToEntity(request.body.asJson.get).map { entity =>
          assert(id == entity.id.toString)
          taskListRepository.store(entity) match {
            case Success((_, taskList: TaskList)) => entityToJson(taskList)
            case Failure(e) => errorMessage(e.getMessage)
          }
        }.fold(errors => BadRequest(validationError(errors)), result => Ok(Json.toJson(result)))
      } else {
        entityNotFound(TaskListIdentifier(id))
      }
    }.get
  }

  def delete(id: String) = Action {
    val identifier = TaskListIdentifier(id)
    taskListRepository.exists(identifier).map { exists =>
      if (exists) {
        taskListRepository.deleteById(identifier) match {
          case Success(_) => NoContent
          case Failure(t) => InternalServerError(t.getMessage)
        }
      } else {
        entityNotFound(identifier)
      }
    }.get
  }


}
