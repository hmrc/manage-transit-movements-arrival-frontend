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

package viewModels

import models.GoodsLocation.BorderForceOffice
import models.UserAnswers
import pages.GoodsLocationPage
import play.api.i18n.Messages
import viewModels.sections._

import javax.inject.Inject

class CheckYourAnswersViewModel @Inject() (
  mrnSection: MrnSection,
  goodsDetailsSection: GoodsDetailsSection,
  traderDetailsSection: TraderDetailsSection,
  consigneeDetailsSection: ConsigneeDetailsSection,
  placeOfNotificationDetailsSection: PlaceOfNotificationDetailsSection,
  eventsSection: EventsSection
) {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Seq[Section] = {

    val mrn                             = mrnSection(userAnswers)
    val goodsDetails                    = goodsDetailsSection(userAnswers)
    lazy val traderDetails              = traderDetailsSection(userAnswers)
    lazy val consigneeDetails           = consigneeDetailsSection(userAnswers)
    lazy val placeOfNotificationDetails = placeOfNotificationDetailsSection(userAnswers)
    val events                          = eventsSection(userAnswers)

    userAnswers.get(GoodsLocationPage) match {
      case Some(BorderForceOffice) => Seq(mrn, goodsDetails, traderDetails, placeOfNotificationDetails, events)
      case _                       => Seq(mrn, goodsDetails, consigneeDetails, events)
    }
  }
}
