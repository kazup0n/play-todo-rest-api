package domain.lifecycle.tasklist

import com.google.inject.ImplementedBy
import domain.model.task.{Task, TaskIdentifier}
import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.SimpleRepository
import infra.IOContext

import scala.util.Try

@ImplementedBy(classOf[JdbcTaskListRepository])
trait TaskListRepository
  extends SimpleRepository[TaskListIdentifier, TaskList] {

  type ID = TaskListIdentifier

  override type This = TaskListRepository

  def storeTask(taskListIdentifier: ID, task: Task)(implicit ctx: IOContext): Try[(This, Task)]

  def deleteTask(taskListIdentifier: ID, taskEntityIdentifier: TaskIdentifier)(implicit ctx: IOContext): Try[This]

  def updateTask(taskListIdentifier: ID, task: Task)(implicit ctx: IOContext): Try[(This, Task)]

}

object TaskListRepository {

  def onMemory(entitiesMap: Map[TaskListIdentifier, TaskList]): TaskListRepository = new OnMemoryTaskListRepository(entitiesMap)

}

