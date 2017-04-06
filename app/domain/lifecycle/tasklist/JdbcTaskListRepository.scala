package domain.lifecycle.tasklist

import domain.model.task.{Task, TaskIdentifier}
import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.JdbcRepository
import infra.IOContext
import infra.jdbc.record.{TaskListRecord, TaskRecord}
import infra.jdbc.{CRUDMapper, TaskDao, TaskListDao}
import play.api.Logger
import scalikejdbc._

import scala.util.Try

class JdbcTaskListRepository extends TaskListRepository
  with JdbcRepository[TaskListIdentifier, TaskList] {

  type E = TaskList

  lazy val mapper: CRUDMapper[TaskListRecord] = TaskListRecord

  override def findById(id: ID)(implicit ctx: IOContext): Try[E] = withDB(ctx) { implicit session =>
    TaskListDao.findById(id.toString).map(recordToEntity)
  }

  override def exists(id: ID)(implicit ctx: IOContext): Try[Boolean] = withDB(ctx) { implicit session =>
    Try {
      mapper.countBy(sqls.eq(mapper.defaultAlias.c(mapper.primaryKeyFieldName), id.toString)) > 0L
    }
  }

  override def findAll()(implicit ctx: IOContext): Try[Seq[E]] = withDB(ctx) { implicit session =>
    TaskListDao.findAll.map(_.map(recordToEntity))
  }

  private def recordToEntity(record: TaskListRecord): TaskList = TaskList(
    TaskListIdentifier(record.id),
    record.name,
    Some(record.version),
    record.tasks.map(recordToTask)
  )

  private def recordToTask(record: TaskRecord): Task = Task(
    TaskIdentifier(record.id),
    record.title,
    record.description,
    Some(record.version)
  )

  private def entityToRecord(entity: TaskList): TaskListRecord = TaskListRecord(
    entity.id.toString,
    entity.name,
    entity.version.getOrElse(1L),
    entity.tasks.map(task => taskToRecord(task, entity.id.toString))
  )

  private def taskToRecord(task: Task, taskListId: String): TaskRecord = TaskRecord(
    task.id.toString,
    taskListId,
    task.title,
    task.description,
    task.version.getOrElse(1L))


  override def store(entity: E)(implicit ctx: IOContext): Try[(JdbcTaskListRepository.this.type, E)] = withDB(ctx) { implicit session =>
    TaskListDao.insertOrUpdate(
      entity.id.toString,
      entityToRecord(entity),
      entity.version
    ).map(recordToEntity)
      .map((this, _))
  }

  override def deleteById(id: ID)(implicit ctx: IOContext): Try[JdbcTaskListRepository.this.type] = withDB(ctx) { implicit session =>
    Logger.info(s"Deleting ${id}")
    for {
      _ <- TaskDao.deleteByTaskListId(id.toString)
      _ <- TaskListDao.deleteById(id.toString, None).map(_ => this)
    } yield {this}
  }

  override def storeTask(taskListIdentifier: ID, task: Task)(implicit ctx: IOContext): Try[(JdbcTaskListRepository, Task)] = withDB(ctx) { implicit session =>
    Logger.debug(s"Updating task(id=${task.id.toString}, taskList=${taskListIdentifier.toString}")
    TaskDao
      .insertOrUpdate(task.id.toString, taskToRecord(task, taskListIdentifier.toString), task.version)
      .map(recordToTask)
      .map(task=>(this, task))
  }

  override def deleteTask(taskListIdentifier: ID, taskEntityIdentifier: TaskIdentifier)(implicit ctx: IOContext): Try[JdbcTaskListRepository] = withDB(ctx) { implicit session =>
    TaskDao.deleteById(taskEntityIdentifier.toString, None).map(_ => this)
  }

  override def updateTask(taskListIdentifier: ID, task: Task)(implicit ctx: IOContext): Try[(JdbcTaskListRepository, Task)] = withDB(ctx) { implicit session =>
    TaskDao
      .insertOrUpdate(task.id.toString, taskToRecord(task, taskListIdentifier.toString), task.version)
      .map(recordToTask)
      .map((this, _))
  }
}
