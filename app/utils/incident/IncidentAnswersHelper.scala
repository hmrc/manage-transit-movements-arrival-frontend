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

import models.incident.IncidentCode
import models.reference.{Country, UnLocode}
import models.{Coordinates, DynamicAddress, Index, Mode, QualifierOfIdentification, UserAnswers}
import pages.incident._
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

import java.time.LocalDate

class IncidentAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  index: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def country: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = IncidentCountryPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.incidentCountry",
    id = Some("change-country")
  )

  def code: Option[SummaryListRow] = getAnswerAndBuildRow[IncidentCode](
    page = IncidentCodePage(index),
    formatAnswer = formatEnumAsText(IncidentCode.messageKeyPrefix),
    prefix = "incident.incidentCode",
    id = Some("change-code")
  )

  def text: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IncidentTextPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.incidentText",
    id = Some("change-text")
  )

  def endorsementYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddEndorsementPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.addEndorsement",
    id = Some("change-add-endorsement")
  )

  def endorsementDate: Option[SummaryListRow] = getAnswerAndBuildRow[LocalDate](
    page = EndorsementDatePage(index),
    formatAnswer = formatAsDate,
    prefix = "incident.endorsementDate",
    id = Some("change-endorsement-date")
  )

  def endorsementAuthority: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EndorsementAuthorityPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.endorsementAuthority",
    id = Some("change-endorsement-authority")
  )

  def endorsementCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = EndorsementCountryPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.endorsementCountry",
    id = Some("change-endorsement-country")
  )

  def endorsementLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EndorsementLocationPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.endorsementLocation",
    id = Some("change-endorsement-location")
  )

  def qualifierOfIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[QualifierOfIdentification](
    page = QualifierOfIdentificationPage(index),
    formatAnswer = formatEnumAsText(QualifierOfIdentification.messageKeyPrefix),
    prefix = "incident.location.qualifierOfIdentification",
    id = Some("change-qualifier-of-identification")
  )

  def unLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = UnLocodePage(index),
    formatAnswer = formatAsText,
    prefix = "incident.location.unLocode",
    id = Some("change-unlocode")
  )

  def coordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = CoordinatesPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.location.coordinates",
    id = Some("change-coordinates")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = AddressPage(index),
    formatAnswer = formatAsText,
    prefix = "incident.location.address",
    id = Some("change-address")
  )

}

object IncidentAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    index: Index
  )(implicit messages: Messages) =
    new IncidentAnswersHelper(userAnswers, mode, index)
}
