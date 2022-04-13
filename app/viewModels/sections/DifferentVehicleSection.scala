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

import models.{CountryList, Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.CheckEventAnswersHelper

class DifferentVehicleSection {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    eventIndex: Index,
    isTranshipment: Boolean,
    codeList: CountryList
  )(implicit messages: Messages): Section = {

    val helper: CheckEventAnswersHelper = new CheckEventAnswersHelper(userAnswers, mode)

    Section(
      sectionTitle = messages("checkEventAnswers.section.title.differentVehicle"),
      rows = Seq(
        if (isTranshipment) helper.isTranshipment(eventIndex) else None,
        helper.transhipmentType(eventIndex),
        helper.transportIdentity(eventIndex),
        helper.transportNationality(eventIndex)(codeList)
      ).flatten
    )

  }
}
