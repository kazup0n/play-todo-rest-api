package domain.model.tasklist

import com.google.common.annotations.VisibleForTesting
import domain.model.task.Task
import domain.support.{Entity, EntityIdentifier}

case class TaskList(
                     id: TaskListIdentifier,
                     name: String,
                     version: Option[Long],
                     tasks: Seq[Task] = Seq.empty
                   ) extends Entity[TaskListIdentifier] {

  override def withVersion(version: Long): TaskList = copy(version = Some(version))
}

object TaskList {

  @VisibleForTesting
  def apply(name: String, tasks: Seq[(String, String)], version: Option[Long]): TaskList =
    TaskList(
      id = TaskListIdentifier(EntityIdentifier.uuid),
      name = name,
      version = version,
      tasks = tasks.map{case (title:String, description:String)=>Task(title, description)}
    )

}