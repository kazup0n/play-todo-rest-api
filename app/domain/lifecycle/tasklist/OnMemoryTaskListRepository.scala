package domain.lifecycle.tasklist

import com.google.inject.Singleton
import domain.model.task.{Task, TaskIdentifier}
import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.{EntityNotFoundException, OnMemorySimpleRepository}
import infra.IOContext

import scala.util.Try

@Singleton
private[tasklist] class OnMemoryTaskListRepository(entities: Map[TaskListIdentifier, TaskList])
  extends OnMemorySimpleRepository[TaskListIdentifier, TaskList](entities)
    with TaskListRepository {


  def this() = this(Map.empty)

  override def storeTask(taskListEntityId: ID, task: Task)(implicit ctx: IOContext): Try[(This, Task)] = Try {
    entities.get(taskListEntityId).map { taskList =>
      val nextTask = task.withVersion(task.version.getOrElse(0L) + 1)
      val dstList = taskList.copy(tasks = taskList.tasks :+ nextTask)
        .withVersion(taskList.version.get + 1)
      val nextInstance = createInstance(
        findContainsTask(nextTask).map { ts =>
          ts.copy(tasks = ts.tasks.filterNot(_.id == nextTask.id))
            .withVersion(ts.version.get + 1)
        }.map { ts =>
          entities.updated(ts.id, ts)
        }.getOrElse(entities).updated(taskListEntityId, dstList)
      )
      (nextInstance, nextTask)
    } match {
      case Some(t) => t
      case None => throw EntityNotFoundException.of(taskListEntityId)
    }
  }

  private def findContainsTask(target: Task): Option[TaskList] = target.version match {
    case Some(_) => entities.values.find(taskList => taskList.tasks.exists(task => task.id == target.id))
    case None => None
  }

  override def deleteTask(taskListIdentifier: ID, taskEntityIdentifier: TaskIdentifier)(implicit ctx: IOContext): Try[TaskListRepository] = Try {
    entities.get(taskListIdentifier).map { taskList =>
      taskList.tasks.find(_.id == taskEntityIdentifier) match {
        case Some(_) => taskList.copy(tasks = taskList.tasks.filterNot(_.id == taskEntityIdentifier))
        case None => throw EntityNotFoundException.of(taskListIdentifier)
      }
    } match {
      case Some(t) => createInstance(entities.updated(taskListIdentifier, t))
      case None => throw EntityNotFoundException.of(taskListIdentifier)
    }
  }

  override def updateTask(taskListIdentifier: ID, task: Task)(implicit ctx: IOContext): Try[(TaskListRepository, Task)] = Try {
    entities.get(taskListIdentifier).map { taskList =>
      val nextTask = task.withVersion(task.version.get + 1)
      val nextList = taskList.copy(tasks = taskList.tasks.filterNot(_.id == task.id) :+ nextTask)
      (createInstance(entities.updated(taskListIdentifier, nextList)), nextTask)
    } match {
      case Some(t) => t
      case None => throw EntityNotFoundException.of(taskListIdentifier)
    }
  }

  protected def createInstance(entitiesMap: Map[ID, TaskList]): This = new OnMemoryTaskListRepository(entitiesMap)
}
