package views.incident.equipment.itemNumber

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView

class ConfirmRemoveItemNumberViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ConfirmRemoveItemNumberView].apply(form, mrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "incident.equipment.itemNumber.confirmRemoveItemNumber"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
