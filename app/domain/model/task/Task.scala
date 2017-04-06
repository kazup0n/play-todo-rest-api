package domain.model.task

import com.google.common.annotations.VisibleForTesting
import domain.support.{Entity, EntityIdentifier}

case class Task(id: TaskIdentifier,
                title: String,
               description: String,
                version: Option[Long]) extends Entity[TaskIdentifier] {
  override def withVersion(version: Long): Task = copy(version = Some(version))
}

object Task {

  @VisibleForTesting
  def apply(title: String, description:String): Task =
    Task(id = TaskIdentifier(EntityIdentifier.uuid),
      title = title,
      description = description,
      version = Some(1L))

}