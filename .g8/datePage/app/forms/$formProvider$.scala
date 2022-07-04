package forms

import java.time.format.DateTimeFormatter
import java.time.{Clock, LocalDate}

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class $formProvider$ @Inject() (clock: Clock) extends Mappings {

    def apply(prefix: String, minimumDate: LocalDate): Form[LocalDate] =
      Form(
        "value" -> localDate(
          invalidKey = s"\$prefix.error.invalid",
          allRequiredKey = s"\$prefix.error.required.all",
          twoRequiredKey = s"\$prefix.error.required.two",
          requiredKey = s"\$prefix.error.required"
        ).verifying(
          minDate(minimumDate, s"\$prefix.error.min.date", minimumDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))),
          maxDate(LocalDate.now(clock), s"\$prefix.error.max.date")
        )
      )
  }
