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
import models.domain.ContainerDomain
import models.{Index, NormalMode, TranshipmentType}
import org.scalacheck.Gen
import pages.events.IsTranshipmentPage
import pages.events.transhipments.{ContainerNumberPage, TranshipmentTypePage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class DifferentContainerSectionSpec extends SpecBase {

  private val sectionText = Gen.alphaNumStr.sample.value

  "must return sections" in {
    val userAnswers = emptyUserAnswers
      .setValue(IsTranshipmentPage(eventIndex), true)
      .setValue(TranshipmentTypePage(eventIndex), TranshipmentType.DifferentContainer)
      .setValue(ContainerNumberPage(eventIndex, Index(0)), ContainerDomain("1"))
      .setValue(ContainerNumberPage(eventIndex, Index(1)), ContainerDomain("2"))
      .setValue(ContainerNumberPage(eventIndex, Index(2)), ContainerDomain("3"))

    val sections = new DifferentContainerSection().apply(userAnswers, NormalMode, eventIndex, sectionText)

    sections.size mustBe 2

    sections.head.sectionTitle.get mustBe sectionText
    sections.head.rows.size mustBe 2
    sections.head.rows.head.value.content mustBe Text("Yes")
    sections.head.rows(1).value.content mustBe Text("A different container")

    sections(1).sectionTitle.get mustBe "Container numbers"
    sections(1).rows.head.value.content mustBe Text("1")
    sections(1).rows(1).value.content mustBe Text("2")
    sections(1).rows(2).value.content mustBe Text("3")
  }

}
