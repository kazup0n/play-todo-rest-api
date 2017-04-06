package controllers

import domain.model.tasklist.{TaskList, TaskListIdentifier}
import domain.support.EntityIdentifier
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

class TaskListControllerSpec extends PlaySpec
  with OneAppPerTest
  with ControllerSpecSupport {

  "TaskListController GET /" should {
    "存在しているすべてのタスクリストを返す" in {
      val taskList = Seq(
        TaskList(
          name = "TaskList",
          tasks = Seq(("task1", "Hard work"), ("task2", "Easy work")),
          version = Some(1L)
        ))

      val response = buildTaskListController(taskList: _*).findAll(FakeRequest())
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")

      contentAsJson(response) mustBe JsArray(taskList.map(toJson))
    }
  }

  "TaskListController POST /" should {
    "タスクリストを作成する" in {
      val taskList = JsObject(Seq("name" -> JsString("taskList")))

      val request = FakeRequest(POST, "").withJsonBody(json = taskList)
      val response = buildTaskListController().create(request)
      status(response) mustBe CREATED
      contentType(response) mustBe Some("application/json")

      val created = contentAsJson(response)

      (created \ "name").as[String] mustBe (taskList \ "name").as[String]
      (created \ "id").as[String] must not be (null)
      (created \ "version").as[Long] must be(1L)
    }

    "不正なフォーマットのJSONをPOSTすると400" in {
      val request = FakeRequest(POST, "").withJsonBody(json = JsObject(Seq("hoge" -> JsString("bar"))))
      val response = buildTaskListController().create(request)
      status(response) mustBe BAD_REQUEST
      contentType(response) mustBe Some("application/json")
      contentAsString(response) must not be (null)
    }

  }

  "TaskListController Patch /{id}" should {
    "タスクリストを更新する" in {
      val taskList = TaskList(
        id = TaskListIdentifier(EntityIdentifier.uuid),
        name = "TaskList",
        tasks = Seq.empty,
        version = Some(1L))

      val controller = buildTaskListController(taskList)
      val request = FakeRequest(PATCH, "").withJsonBody(json = toJson(taskList.copy(name = "TaskList2")))
      val response = controller.update(taskList.id.toString)(request)

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe toJson(taskList.copy(name = "TaskList2", version = Some(taskList.version.get + 1)))

    }
  }

  "TaskListController DELETE /{id}" should {
    "タスクリストを削除する" in {
      val taskList = TaskList("taskList", Seq.empty, Some(1L))
      val response = buildTaskListController(taskList).delete(taskList.id.toString)(FakeRequest(DELETE, ""))
      status(response) mustBe NO_CONTENT
      contentType(response) mustBe None
    }

    "指定したIDが存在しない場合404" in {
      val taskList = TaskList("taskList", Seq.empty, Some(1L))
      val response = buildTaskListController(taskList).delete("123")(FakeRequest(DELETE, ""))
      status(response) mustBe NOT_FOUND
      contentType(response) mustBe Some("application/json")
    }
  }

}
