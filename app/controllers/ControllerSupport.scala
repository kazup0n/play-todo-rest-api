package controllers

import domain.support.EntityIdentifier
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsObject, JsPath, JsString}
import play.api.mvc.Controller

trait ControllerSupport extends Controller {

  protected[controllers] def entityNotFound(entityIdentifier: EntityIdentifier) =
    NotFound(errorMessage(s"Entity not found(id=${entityIdentifier.toString})"))

  protected[controllers] def errorMessage(msg: String) = JsObject(Seq("message" -> JsString(msg)))

  protected[controllers] def validationError(errors: Seq[(JsPath, Seq[ValidationError])]) =
    errorMessage(
      errors.map {
        case (path: JsPath, es: Seq[ValidationError]) => path.toString + ":" + es.map(_.message).mkString(",")
      }.mkString("\n"))

}
