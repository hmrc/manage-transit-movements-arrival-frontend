package forms

import forms.mappings.Mappings
import models.UkAddress
import models.AddressLine._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import javax.inject.Inject

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, name: String)(implicit messages: Messages): Form[UkAddress] =
    Form(
      mapping(
        AddressLine1.field -> {
          lazy val args = Seq(AddressLine1.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine1.length, s"\$prefix.error.length", Seq(AddressLine1.arg.capitalize, name, AddressLine1.length)),
                regexp(AddressLine1.regex, s"\$prefix.error.invalid", Seq(AddressLine1.arg.capitalize, name))
              )
            )
        },
        AddressLine2.field -> {
          lazy val args = Seq(AddressLine2.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine2.length, s"\$prefix.error.length", Seq(AddressLine2.arg.capitalize, name, AddressLine2.length)),
                regexp(AddressLine2.regex, s"\$prefix.error.invalid", Seq(AddressLine2.arg.capitalize, name))
              )
            )
        },
        UkPostCode.field -> {
          lazy val args = Seq(name)
          trimmedText(s"\$prefix.error.postalCode.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(UkPostCode.length, s"\$prefix.error.postalCode.length", args :+ PostalCode.length),
                regexp(UkPostCode.regex, s"\$prefix.error.postalCode.invalid", args)
              )
            )
        }
      )(UkAddress.apply)(UkAddress.unapply)
    )
}
