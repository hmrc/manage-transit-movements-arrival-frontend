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

import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle, DifferentVehicle}
import models.{CountryList, Index, Mode, UserAnswers}
import pages.events.transhipments.TranshipmentTypePage
import play.api.i18n.Messages

import javax.inject.Inject

class EventTypeSection @Inject() (
  vehicleInformationSection: VehicleInformationSection,
  differentContainerSection: DifferentContainerSection,
  differentVehicleSection: DifferentVehicleSection
) {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    eventIndex: Index,
    isTranshipment: Boolean,
    codeList: CountryList
  )(implicit messages: Messages): Seq[Section] =
    userAnswers
      .get(TranshipmentTypePage(eventIndex))
      .map {
        case DifferentVehicle =>
          differentVehicleSection(userAnswers, mode, eventIndex, isTranshipment, codeList) :: Nil
        case DifferentContainer =>
          differentContainerSection(userAnswers, mode, eventIndex, "checkEventAnswers.section.title.differentContainer")
        case DifferentContainerAndVehicle =>
          differentContainerSection(userAnswers, mode, eventIndex, "checkEventAnswers.section.title.differentContainerAndVehicle") :+
            vehicleInformationSection(userAnswers, mode, eventIndex, codeList)
      }
      .getOrElse(Seq.empty)
}
