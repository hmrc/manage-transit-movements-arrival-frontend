package forms

import forms.behaviours.DateBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

import java.time.{Clock, LocalDate, ZoneOffset}

class $formProvider$Spec extends DateBehaviours {
  private val prefix = Gen.alphaNumStr.sample.value

  private val (minDate, minDateAsString) = (LocalDate.of(2020: Int, 12: Int, 31: Int), "31 December 2020")
  private val maxDate                    = LocalDate.now(ZoneOffset.UTC)
  private val zone                       = ZoneOffset.UTC
  private val clock                      = Clock.systemDefaultZone.withZone(zone)
  private val form                       = new $formProvider$(clock)(prefix, minDate)

  ".value" - {

    val fieldName = "value"

    val validData = datesBetween(
      min = minDate,
      max = LocalDate.now(zone)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, fieldName, s"\$prefix.error.required.all")

    behave like dateFieldWithMin(form, fieldName, min = minDate, FormError("value", s"\$prefix.error.min.date", Seq(minDateAsString)))

    behave like dateFieldWithMax(form, fieldName, max = maxDate, FormError("value", s"\$prefix.error.max.date"))

  }
}
