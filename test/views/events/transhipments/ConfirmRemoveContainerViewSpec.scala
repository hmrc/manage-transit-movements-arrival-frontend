/*
 * Copyright 2022 HM Revenue & Customs
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

package views.events.transhipments

import forms.events.transhipments.ConfirmRemoveContainerFormProvider
import generators.MessagesModelGenerators
import models.NormalMode
import models.domain.ContainerDomain
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.events.transhipments.ConfirmRemoveContainerView

class ConfirmRemoveContainerViewSpec extends YesNoViewBehaviours with MessagesModelGenerators {

  private val containerDomain: ContainerDomain = arbitrary[ContainerDomain].sample.value

  override def form: Form[Boolean] = new ConfirmRemoveContainerFormProvider()(containerDomain)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[ConfirmRemoveContainerView]
      .apply(form, mrn, eventIndex, containerIndex, NormalMode, containerDomain.containerNumber)(fakeRequest, messages)

  override val prefix: String = "confirmRemoveContainer"

  behave like pageWithTitle(containerDomain.containerNumber)

  behave like pageWithBackLink

  behave like pageWithHeading(containerDomain.containerNumber)

  behave like pageWithRadioItems(args = Seq(containerDomain.containerNumber))
}
