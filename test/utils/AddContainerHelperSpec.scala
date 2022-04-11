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

package utils

import base.SpecBase
import controllers.events.transhipments.routes.{ConfirmRemoveContainerController, ContainerNumberController}
import models.domain.ContainerDomain
import models.{CheckMode, Mode}
import pages.events.transhipments.ContainerNumberPage
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddContainerHelperSpec extends SpecBase {

  val mode: Mode = CheckMode

  "AddContainerHelper" - {

    val container = ContainerDomain("NUMBER")

    ".containerListItem" - {

      "must return None" - {
        "when ContainerNumberPage undefined" in {

          val helper = new AddContainerHelper(emptyUserAnswers, mode)
          helper.containerListItem(eventIndex, containerIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when ContainerNumberPage defined" in {

          val answers = emptyUserAnswers
            .setValue(ContainerNumberPage(eventIndex, containerIndex), container)

          val helper = new AddContainerHelper(answers, mode)
          helper.containerListItem(eventIndex, containerIndex) mustBe Some(
            ListItem(
              name = container.containerNumber,
              changeUrl = ContainerNumberController.onPageLoad(mrn, eventIndex, containerIndex, mode).url,
              removeUrl = ConfirmRemoveContainerController.onPageLoad(mrn, eventIndex, containerIndex, mode).url
            )
          )
        }
      }
    }
  }

}
