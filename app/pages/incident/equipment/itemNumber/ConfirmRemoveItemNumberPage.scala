package pages.incident.equipment.itemNumber

import controllers.incident.equipment.itemNumber.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.ItemSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object ConfirmRemoveItemNumberPage extends QuestionPage[Boolean] {

  override def path: JsPath = ItemSection.path \ toString

  override def toString: String = "confirmRemoveItemNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.ConfirmRemoveItemNumberController.onPageLoad(userAnswers.mrn, mode))
}
