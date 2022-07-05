package views.$package$

import forms.$formProvider$
import generators.Generators
import models.{UkAddress, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.AddressViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends AddressViewBehaviours with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  override def form: Form[UkAddress] = new $formProvider$()(prefix, addressHolderName)

  override def applyView(form: Form[UkAddress]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, mrn, NormalMode, addressHolderName)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Continue")
}
