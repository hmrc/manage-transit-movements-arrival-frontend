package forms

import forms.mappings.Mappings
import models.UkAddress
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import javax.inject.Inject
import models.UkAddressLine.{BuildingAndStreet, City, PostCode}

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, name: String)(implicit messages: Messages): Form[UkAddress] =
    Form(
      mapping(
        BuildingAndStreet.field -> {
          lazy val args = Seq(BuildingAndStreet.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(BuildingAndStreet.length, s"\$prefix.error.length", Seq(BuildingAndStreet.arg.capitalize, name, BuildingAndStreet.length)),
                regexp(BuildingAndStreet.regex, s"\$prefix.error.invalid", Seq(BuildingAndStreet.arg.capitalize, name))
              )
            )
        },
        City.field -> {
          lazy val args = Seq(City.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(City.length, s"\$prefix.error.length", Seq(City.arg.capitalize, name, City.length)),
                regexp(City.regex, s"\$prefix.error.invalid", Seq(City.arg.capitalize, name))
              )
            )
        },
        PostCode.field -> {
          lazy val args = Seq(name)
          trimmedText(s"\$prefix.error.postalCode.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostCode.length, s"\$prefix.error.postalCode.length", args :+ PostCode.length),
                regexp(PostCode.regex, s"\$prefix.error.postalCode.invalid", args)
              )
            )
        }
      )(UkAddress.apply)(UkAddress.unapply)
    )
}
