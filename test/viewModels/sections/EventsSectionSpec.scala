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

package viewModels.sections

import base.SpecBase
import generators.Generators
import models.Index
import pages.IncidentOnRoutePage
import pages.events.EventPlacePage
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class EventsSectionSpec extends SpecBase with Generators {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(IncidentOnRoutePage, true)
      .setValue(EventPlacePage(Index(0)), "1")
      .setValue(EventPlacePage(Index(1)), "2")
      .setValue(EventPlacePage(Index(2)), "3")

    val section = new EventsSection().apply(userAnswers)

    section.sectionTitle.get mustBe "Events"
    section.rows.size mustBe 4
    section.rows.head.value.content mustBe Text("Yes")
    section.rows(1).value.content mustBe Text("1")
    section.rows(2).value.content mustBe Text("2")
    section.rows(3).value.content mustBe Text("3")
  }
}
