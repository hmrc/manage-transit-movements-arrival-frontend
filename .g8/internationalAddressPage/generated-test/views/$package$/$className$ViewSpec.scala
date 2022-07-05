package views.$package$

import forms.$formProvider$
import generators.Generators
import models.{Address, CountryList, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InternationalAddressViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends InternationalAddressViewBehaviours with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val countryList = arbitrary[CountryList].sample.value

  override def form: Form[Address] = new $formProvider$()(prefix, addressHolderName, countryList)

  override def applyView(form: Form[Address]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, mrn, NormalMode, countryList.countries, addressHolderName)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Continue")
}
