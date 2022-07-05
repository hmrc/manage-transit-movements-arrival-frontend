package views.$package$

import forms.$formProvider$
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.$package$.$className$View
import java.time.{Clock, LocalDate, ZoneOffset}

class $className$ViewSpec extends DateInputViewBehaviours {

  private val minDate = LocalDate.of(2020: Int, 12: Int, 31: Int) //"31 December 2020"
  private val zone                       = ZoneOffset.UTC
  private val clock                      = Clock.systemDefaultZone.withZone(zone)

  override def form: Form[LocalDate] = new $formProvider$(clock)(prefix, minDate)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, mrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Continue")
}
