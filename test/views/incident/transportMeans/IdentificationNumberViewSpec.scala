package views.incident.transportMeans

import forms.IdentificationNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.incident.transportMeans.IdentificationNumberView
import org.scalacheck.{Arbitrary, Gen}

class IdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "incident.transportMeans.identificationNumber"

  override def form: Form[String] = new IdentificationNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[IdentificationNumberView].apply(form, mrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
