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
import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle, DifferentVehicle}
import models.{CountryList, Index, Mode, UserAnswers}
import pages.events.transhipments.TranshipmentTypePage
import play.api.i18n.Messages
import utils.CheckEventAnswersHelper

object EventTypeSection {

  def apply(userAnswers: UserAnswers, mode: Mode, eventIndex: Index, isTranshipment: Boolean, codeList: CountryList)(implicit
    messages: Messages
  ): Seq[Section] =
    userAnswers
      .get(TranshipmentTypePage(eventIndex))
      .map {
        case DifferentVehicle =>
          DifferentVehicleSection(userAnswers, mode, eventIndex, isTranshipment, codeList)
        case DifferentContainer =>
          DifferentContainerSection(userAnswers, mode, eventIndex, isTranshipment, "checkEventAnswers.section.title.differentContainer")
        case DifferentContainerAndVehicle =>
          DifferentContainerSection(userAnswers, mode, eventIndex, isTranshipment, "checkEventAnswers.section.title.differentContainerAndVehicle") ++
            VehicleInformationSection(userAnswers, mode, eventIndex, codeList)
      }
      .getOrElse(Seq.empty)
}

object VehicleInformationSection {

  def apply(userAnswers: UserAnswers, mode: Mode, eventIndex: Index, codeList: CountryList)(implicit messages: Messages): Seq[Section] = {

    val helper = new CheckEventAnswersHelper(userAnswers, mode)

    Seq(
      Section(
        messages("checkEventAnswers.section.title.vehicleInformation"),
        Seq(
          helper.transportIdentity(eventIndex),
          helper.transportNationality(eventIndex)(codeList)
        ).flatten
      )
    )
  }
}

object DifferentContainerSection {

  def apply(userAnswers: UserAnswers, mode: Mode, eventIndex: Index, isTranshipment: Boolean, sectionText: String)(implicit
    messages: Messages
  ): Seq[Section] = {

    val helper: CheckEventAnswersHelper = new CheckEventAnswersHelper(userAnswers, mode)

    Seq(
      Some(
        Section(messages(sectionText), Seq(helper.isTranshipment(eventIndex), helper.transhipmentType(eventIndex)).flatten)
      ),
      userAnswers
        .get(DeriveNumberOfContainers(eventIndex))
        .map {
          containerCount =>
            val listOfContainerIndexes = List.range(0, containerCount).map(Index(_))
            val rows = listOfContainerIndexes.flatMap {
              index =>
                helper.containerNumber(eventIndex, index)
            }
            Section(messages("checkEventAnswers.section.title.containerNumbers"), rows)
        }
    ).flatten
  }
}

object DifferentVehicleSection {

  def apply(userAnswers: UserAnswers, mode: Mode, eventIndex: Index, isTranshipment: Boolean, codeList: CountryList)(implicit
    messages: Messages
  ): Seq[Section] = {

    val helper: CheckEventAnswersHelper = new CheckEventAnswersHelper(userAnswers, mode)

    Seq(
      Section(
        messages("checkEventAnswers.section.title.differentVehicle"),
        Seq(
          if (isTranshipment) helper.isTranshipment(eventIndex) else None,
          helper.transhipmentType(eventIndex),
          helper.transportIdentity(eventIndex),
          helper.transportNationality(eventIndex)(codeList)
        ).flatten
      )
    )
  }
}
