package forms

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.AddressLine._
import models.reference.{Country, CountryCode}
import models.{AddressLine, CountryList}
import org.scalacheck.Gen
import play.api.data.FormError

class $formProvider$Spec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value
  private val name   = Gen.alphaNumStr.sample.value

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))

  private val requiredKey = s"\$prefix.error.required"
  private val lengthKey   = s"\$prefix.error.length"
  private val invalidKey  = s"\$prefix.error.invalid"

  private val form = new AddressFormProvider()(prefix, name, countries)

  ".addressLine1" - {

    val fieldName = AddressLine1.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine1.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine1.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine1.arg, name, AddressLine1.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine1.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine1.arg, name)),
      length = AddressLine1.length
    )
  }

  ".addressLine2" - {

    val fieldName = AddressLine2.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine2.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine2.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine2.arg, name, AddressLine2.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine2.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine2.arg, name)),
      length = AddressLine2.length
    )
  }

  ".postalCode" - {

    val postcodeInvalidKey = s"\$prefix.error.postalCode.invalid"

    val fieldName = UkPostCode.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(UkPostCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = UkPostCode.length,
      lengthError = FormError(fieldName, lengthKey, Seq(UkPostCode.arg, name, PostalCode.length))
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(UkPostCode.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, postcodeInvalidKey, Seq(name)),
      length = UkPostCode.length
    )
  }
}
