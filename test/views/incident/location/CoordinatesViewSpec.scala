/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.incident.location

import forms.CoordinatesFormProvider
import generators.Generators
import models.{Coordinates, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CoordinatesViewBehaviours
import views.html.incident.location.CoordinatesView

class CoordinatesViewSpec extends CoordinatesViewBehaviours with Generators {

  override val prefix: String = "incident.location.coordinates"

  override def form: Form[Coordinates] = new CoordinatesFormProvider()(prefix)

  override def applyView(form: Form[Coordinates]): HtmlFormat.Appendable =
    injector.instanceOf[CoordinatesView].apply(form, mrn, NormalMode, index)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithHint("These numbers can be positive or negative and need between 5 and 7 decimal places. For example, 50.96622 or 1.86201.")

  behave like pageWithCoordinatesInput()

  behave like pageWithSubmitButton("Continue")
}
