package pages.$package$

import controllers.$package$.routes
import models.reference.$referenceClass$
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.$pageSection$
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object $className$Page extends QuestionPage[$referenceClass$] {

  override def path: JsPath = $pageSection$.path \ toString

  override def toString: String = "$className;format="decap"$"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.$className;format="cap"$Controller.onPageLoad(userAnswers.mrn, mode))
}
