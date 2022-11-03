package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form
import models.reference.UnLocode
import models.UnLocodeList

class UnLocodeFormProvider @Inject() extends Mappings {

  def apply(prefix: String, unLocodes: UnLocodeList): Form[UnLocode] =

    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(s"$prefix.error.required", value => unLocodes.getAll.exists(_.id == value))
        .transform[UnLocode](value => unLocodes.getUnLocode(value).get, _.id)
    )
}
