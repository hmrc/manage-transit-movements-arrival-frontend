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

import derivable.DeriveNumberOfContainers
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.CheckEventAnswersHelper

class DifferentContainerSection {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    eventIndex: Index,
    sectionText: String
  )(implicit messages: Messages): Seq[Section] = {

    val helper: CheckEventAnswersHelper = new CheckEventAnswersHelper(userAnswers, mode)

    Seq(
      Some(
        Section(
          sectionTitle = messages(sectionText),
          rows = Seq(
            helper.isTranshipment(eventIndex),
            helper.transhipmentType(eventIndex)
          ).flatten
        )
      ),
      userAnswers
        .get(DeriveNumberOfContainers(eventIndex))
        .map {
          containerCount =>
            val rows = (0 to containerCount).flatMap {
              x =>
                helper.container(eventIndex, Index(x))
            }
            Section(
              sectionTitle = messages("checkEventAnswers.section.title.containerNumbers"),
              rows = rows
            )
        }
    ).flatten
  }
}
