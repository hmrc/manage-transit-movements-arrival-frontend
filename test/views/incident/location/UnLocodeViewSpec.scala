package views.incident.location

import forms.UnLocodeFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.UnLocode
import models.UnLocodeList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.incident.location.UnLocodeView

class UnLocodeViewSpec extends InputSelectViewBehaviours[UnLocode] {

  override def form: Form[UnLocode] = new UnLocodeFormProvider()(prefix, UnLocodeList(values))

  override def applyView(form: Form[UnLocode]): HtmlFormat.Appendable =
    injector.instanceOf[UnLocodeView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[UnLocode] = arbitraryUnLocode

  override val prefix: String = "incident.location.unLocode"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("What is the UN/LOCODE for the incident hint")

  behave like pageWithSubmitButton("Continue")
}
