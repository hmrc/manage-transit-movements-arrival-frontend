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

package viewModels

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.LocationOfGoodsAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class LocationOfGoodsAnswersViewModel(section: Section)

object LocationOfGoodsAnswersViewModel {

  class LocationOfGoodsAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): LocationOfGoodsAnswersViewModel = {

      val helper = LocationOfGoodsAnswersHelper(userAnswers, mode)

      val section = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.locationOfGoods.subheading"),
        rows = Seq(
          helper.locationType,
          helper.qualifierOfIdentification,
          helper.customsOfficeIdentifier,
          helper.identificationNumber,
          helper.authorisationNumber,
          helper.coordinates,
          helper.unLocode,
          helper.country,
          helper.address,
          helper.contactYesNo,
          helper.contactName,
          helper.contactPhoneNumber
        ).flatten
      )

      new LocationOfGoodsAnswersViewModel(section)
    }
  }
}
