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

package utils.incident

import models.journeyDomain.incident.equipment.itemNumber.ItemNumberDomain
import models.journeyDomain.incident.equipment.seal.SealDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

class EquipmentAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index,
  equipmentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def containerIdentificationNumberYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.equipment.containerIdentificationNumberYesNo",
    id = Some("change-add-container-identification-number")
  )

  def containerIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.equipment.containerIdentificationNumber",
    id = Some("change-container-identification-number")
  )

  def sealsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddSealsYesNoPage(incidentIndex, equipmentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.equipment.addSealsYesNo",
    id = Some("change-add-seals")
  )

  def goodsItemNumbersYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.equipment.addGoodsItemNumberYesNo",
    id = Some("change-add-goods-item-numbers")
  )

  def seal(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[SealDomain](
    formatAnswer = formatAsText,
    prefix = "incident.equipment.seal",
    id = Some(s"change-seal-${index.display}"),
    args = index.display
  )(SealDomain.userAnswersReader(incidentIndex, equipmentIndex, index))

  def goodsItemNumber(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[ItemNumberDomain](
    formatAnswer = formatAsText,
    prefix = "incident.equipment.goodsItemNumber",
    id = Some(s"change-goods-item-number-${index.display}"),
    args = index.display
  )(ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, index))

}

object EquipmentAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    incidentIndex: Index,
    equipmentIndex: Index
  )(implicit messages: Messages) =
    new EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
}
