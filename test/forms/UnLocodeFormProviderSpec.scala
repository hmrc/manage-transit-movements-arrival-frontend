package forms

import forms.behaviours.StringFieldBehaviours
import models.UnLocodeList
import play.api.data.FormError
import generators.Generators
import org.scalacheck.Gen

class UnLocodeFormProviderSpec extends StringFieldBehaviours with Generators{

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val unLocode1 = arbitraryUnLocode.arbitrary.sample.get
  private val unLocode2 = arbitraryUnLocode.arbitrary.sample.get
  private val unLocodeList = UnLocodeList(Seq(unLocode1, unLocode2))

  private val form = new UnLocodeFormProvider()(prefix, unLocodeList)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind if customs office id does not exist in the unLocodeList" in {
      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a unLocode id which is in the list" in {
      val boundForm = form.bind(Map("value" -> unLocode1.id))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
