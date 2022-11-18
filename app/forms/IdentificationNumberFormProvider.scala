package forms

import forms.Constants.identificationNumberLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex.alphaNumericRegex

import javax.inject.Inject
import play.api.data.Form

class IdentificationNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String, args: String*): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required", args)
        .verifying(
          StopOnFirstFail[String](
            maxLength(identificationNumberLength, s"$prefix.error.length", args),
            regexp(alphaNumericRegex, s"$prefix.error.invalid", args)
          )
        )
    )
