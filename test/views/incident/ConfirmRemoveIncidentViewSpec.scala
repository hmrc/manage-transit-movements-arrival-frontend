package views.incident

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.ConfirmRemoveIncidentView

class ConfirmRemoveIncidentViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ConfirmRemoveIncidentView].apply(form, mrn, NormalMode, incidentIndex)(fakeRequest, messages)

  override val prefix: String = "incident.remove"

  behave like pageWithTitle(incidentIndex.display)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(incidentIndex.display)

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
