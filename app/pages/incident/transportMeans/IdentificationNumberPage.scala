package pages.incident.transportMeans

import controllers.incident.transportMeans.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident.TransportMeansSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object IdentificationNumberPage extends QuestionPage[String] {

  override def path: JsPath = TransportMeansSection.path \ toString

  override def toString: String = "identificationNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.IdentificationNumberController.onPageLoad(userAnswers.mrn, mode))
}
