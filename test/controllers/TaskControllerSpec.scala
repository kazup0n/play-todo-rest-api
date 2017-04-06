package controllers

import domain.lifecycle.tasklist.TaskListRepository
import domain.model.task.TaskIdentifier
import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.EntityNotFoundException
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.libs.json.{JsObject, JsString}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.util.{Failure, Success}

class TaskControllerSpec extends PlaySpec
  with OneAppPerTest
  with MockitoSugar
  with ControllerSpecSupport {

  def buildTaskController(taskLists: TaskList*) = new TaskController(buildRepository(taskLists: _*), contextProvider)

  "TaskController tasklist/{}/tasks/ POST" should {

    "タスクを生成する" in {
      val task = JsObject(Seq("title" -> JsString("Milk"), "description" -> JsString("Buy milk")))
      val list = TaskList("SHOPPING", Seq.empty, Some(1L))
      val response = buildTaskController(list).create(list.id.toString)(FakeRequest(POST, "").withJsonBody(task))
      status(response) mustBe CREATED
      contentType(response) mustBe Some("application/json")

      (contentAsJson(response) \ "title").as[String] mustBe "Milk"
      (contentAsJson(response) \ "id").as[String] must not be (null)
      (contentAsJson(response) \ "version").as[Long] mustBe 1L
    }

    "存在しないリストを指定すると404" in {
      val response = buildTaskController().create("1234")(
        FakeRequest(POST, "")
          .withJsonBody(JsObject(Seq("title" -> JsString("Milk"), "description" -> JsString("Buy milk")))))

      status(response) mustBe NOT_FOUND
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "message").as[String] must not be (null)

    }

    "不正なJSONを指定すると 400" in {
      val response = buildTaskController(TaskList("SHOPPING", Seq.empty, Some(1L)))
        .create("1")(FakeRequest(POST, "").withJsonBody(JsObject(Seq("hoge" -> JsString("hello")))))
      status(response) mustBe BAD_REQUEST
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "message").as[String].length must not be 0
    }

  }

  "TaskController /{taskList}/tasks/{taskId} DELETE" should {

    "タスクを削除する" in {
      val repository = mock[TaskListRepository]
      val controller = new TaskController(repository, contextProvider)

      when(repository.deleteTask(any[TaskListIdentifier], any[TaskIdentifier])(any()))
        .thenReturn(Success(repository))


      val response = controller.delete("1", "1")(FakeRequest(DELETE, ""))

      status(response) mustBe NO_CONTENT
      contentType(response) mustBe None
    }

    "存在しないリストを指定すると404" in {
      val repository = mock[TaskListRepository]
      val controller = new TaskController(repository, contextProvider)
      val ex = EntityNotFoundException.of(TaskListIdentifier("1"))

      when(repository.deleteTask(any[TaskListIdentifier], any[TaskIdentifier])(any()))
        .thenReturn(Failure(ex))

      val response = controller.delete("1", "1")(FakeRequest(DELETE, ""))
      status(response) mustBe NOT_FOUND
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "message").as[String] mustBe "Entity not found(id=1)"
    }

    "存在しないタスクを指定すると404" in {
      val repository = mock[TaskListRepository]
      val controller = new TaskController(repository, contextProvider)
      val ex = EntityNotFoundException.of(TaskIdentifier("1"))

      when(repository.deleteTask(any[TaskListIdentifier], any[TaskIdentifier])(any()))
        .thenReturn(Failure(ex))


      val response = controller.delete("1", "1")(FakeRequest(DELETE, ""))
      status(response) mustBe NOT_FOUND
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "message").as[String] mustBe "Entity not found(id=1)"
    }

  }

}
