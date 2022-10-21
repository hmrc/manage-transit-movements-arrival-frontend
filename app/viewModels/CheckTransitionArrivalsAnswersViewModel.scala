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

import models.{Index, Mode, RichOptionJsArray, UserAnswers}
import pages.sections.AuthorisationsSection
import play.api.i18n.Messages
import utils.CheckTransitionArrivalsHelper
import viewModels.sections.Section

import javax.inject.Inject

case class CheckTransitionArrivalsAnswersViewModel(sections: Seq[Section])

object CheckTransitionArrivalsAnswersViewModel {

  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckTransitionArrivalsAnswersViewModel =
    new CheckTransitionArrivalsAnswersViewModelProvider().apply(userAnswers, mode)

  class CheckTransitionArrivalsAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckTransitionArrivalsAnswersViewModel = {

      val helper = new CheckTransitionArrivalsHelper(userAnswers, mode)

      val identificationSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.identification.subheading"),
        rows = Seq(
          Some(helper.movementReferenceNumber),
          helper.destinationOffice,
          helper.identificationNumber,
          helper.isSimplified
        ).flatten
      )

      val authorisationsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.authorisations.subheading"),
        rows = userAnswers
          .get(AuthorisationsSection)
          .mapWithIndex {
            (_, index) => helper.authorisation(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove",
          text = messages("arrivals.checkYourAnswers.authorisations.addOrRemove"),
          href = controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn).url
        )
      )

      val locationOfGoodsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.locationOfGoods.subheading"),
        rows = Seq(
          helper.locationType,
          helper.qualifierOfIdentification,
          helper.customsOfficeIdentifier,
          helper.locationOfGoodsIdentificationNumber,
          helper.authorisationNumber,
          helper.coordinates,
          helper.unLocode,
          helper.address,
          helper.postalCode,
          helper.additionalIdentifierYesNo,
          helper.additionalIdentifier,
          helper.contactYesNo,
          helper.contactName,
          helper.contactPhoneNumber
        ).flatten
      )

      new CheckTransitionArrivalsAnswersViewModel(Seq(identificationSection, authorisationsSection, locationOfGoodsSection))
    }
  }
}
