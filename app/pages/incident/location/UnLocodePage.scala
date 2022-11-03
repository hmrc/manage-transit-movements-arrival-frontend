package pages.incident.location

import controllers.incident.location.routes
import models.{Mode, UserAnswers}
import models.reference.UnLocode
import pages.QuestionPage
import pages.sections.IncidentSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object UnLocodePage extends QuestionPage[UnLocode] {

  override def path: JsPath = IncidentSection.path \ toString

  override def toString: String = "unLocode"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.UnLocodeController.onPageLoad(userAnswers.mrn, mode))
}
