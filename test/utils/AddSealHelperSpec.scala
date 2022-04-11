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
import controllers.events.seals.routes._
import models.domain.SealDomain
import models.{CheckMode, Mode}
import pages.events.seals.SealIdentityPage
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddSealHelperSpec extends SpecBase {

  val mode: Mode = CheckMode

  "AddSealHelper" - {

    val seal = SealDomain("NUMBER")

    ".sealListItem" - {

      "must return None" - {
        "when SealIdentityPage undefined" in {

          val helper = new AddSealHelper(emptyUserAnswers, mode)
          helper.sealListItem(eventIndex, sealIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when SealIdentityPage defined" in {

          val answers = emptyUserAnswers
            .setValue(SealIdentityPage(eventIndex, sealIndex), seal)

          val helper = new AddSealHelper(answers, mode)
          helper.sealListItem(eventIndex, sealIndex) mustBe Some(
            ListItem(
              name = seal.numberOrMark,
              changeUrl = SealIdentityController.onPageLoad(mrn, eventIndex, sealIndex, mode).url,
              removeUrl = ConfirmRemoveSealController.onPageLoad(mrn, eventIndex, sealIndex, mode).url
            )
          )
        }
      }
    }
  }

}
