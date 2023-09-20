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

package utils.incident

import models.incident.IncidentCode
import models.incident.transportMeans.Identification
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.reference.{Country, Nationality}
import models.{Coordinates, DynamicAddress, Index, Mode, QualifierOfIdentification, UserAnswers}
import pages.incident._
import pages.incident.endorsement.{EndorsementAuthorityPage, EndorsementCountryPage, EndorsementDatePage, EndorsementLocationPage}
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}
import pages.incident.transportMeans._
import pages.sections.incident.EquipmentsSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper
import viewModels.Link

import java.time.LocalDate

class IncidentAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def equipments: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(EquipmentsSection(incidentIndex))(equipment)

  def equipment(index: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[EquipmentDomain](
      formatAnswer = _.asString.toText,
      prefix = "incident.equipment",
      id = Some(s"change-transport-equipment-${index.display}"),
      args = index.display
    )(EquipmentDomain.userAnswersReader(incidentIndex, index))

  def addOrRemoveEquipments: Option[Link] = buildLink(EquipmentsSection(incidentIndex)) {
    Link(
      id = "add-or-remove-transport-equipment",
      text = messages("arrivals.checkYourAnswers.equipments.addOrRemove"),
      href = controllers.incident.equipment.routes.AddAnotherEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url
    )
  }

  def country: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = IncidentCountryPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.incidentCountry",
    id = Some("change-country")
  )

  def code: Option[SummaryListRow] = getAnswerAndBuildRow[IncidentCode](
    page = IncidentCodePage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.incidentCode",
    id = Some("change-code")
  )

  def text: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IncidentTextPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.incidentText",
    id = Some("change-text")
  )

  def endorsementYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddEndorsementPage(incidentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.addEndorsement",
    id = Some("change-add-endorsement")
  )

  def endorsementDate: Option[SummaryListRow] = getAnswerAndBuildRow[LocalDate](
    page = EndorsementDatePage(incidentIndex),
    formatAnswer = formatAsDate,
    prefix = "incident.endorsement.date",
    id = Some("change-endorsement-date")
  )

  def endorsementAuthority: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EndorsementAuthorityPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.endorsement.authority",
    id = Some("change-endorsement-authority")
  )

  def endorsementCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = EndorsementCountryPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.endorsement.country",
    id = Some("change-endorsement-country")
  )

  def endorsementLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EndorsementLocationPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.endorsement.location",
    id = Some("change-endorsement-location")
  )

  def qualifierOfIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[QualifierOfIdentification](
    page = QualifierOfIdentificationPage(incidentIndex),
    formatAnswer = formatEnumAsText(QualifierOfIdentification.messageKeyPrefix),
    prefix = "incident.location.qualifierOfIdentification",
    id = Some("change-qualifier-of-identification")
  )

  def unLocode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UnLocodePage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.location.unLocode",
    id = Some("change-unlocode")
  )

  def coordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = CoordinatesPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.location.coordinates",
    id = Some("change-coordinates")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = AddressPage(incidentIndex),
    formatAnswer = formatAsDynamicAddress,
    prefix = "incident.location.address",
    id = Some("change-address")
  )

  def containerIndicatorYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ContainerIndicatorYesNoPage(incidentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.containerIndicatorYesNo",
    id = Some("change-add-container-indicator")
  )

  def transportEquipmentYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddTransportEquipmentPage(incidentIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.addTransportEquipment",
    id = Some("change-add-transport-equipment")
  )

  def transportMeansIdentificationType: Option[SummaryListRow] = getAnswerAndBuildRow[Identification](
    page = IdentificationPage(incidentIndex),
    formatAnswer = formatEnumAsText(Identification.messageKeyPrefix),
    prefix = "incident.transportMeans.identification",
    id = Some("change-transport-means-identification-type")
  )

  def transportMeansIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.transportMeans.identificationNumber",
    id = Some("change-transport-means-identification-number")
  )

  def transportMeansRegisteredCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Nationality](
    page = TransportNationalityPage(incidentIndex),
    formatAnswer = formatAsText,
    prefix = "incident.transportMeans.transportNationality",
    id = Some("change-transport-means-registered-country")
  )

}

object IncidentAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    incidentIndex: Index
  )(implicit messages: Messages) =
    new IncidentAnswersHelper(userAnswers, mode, incidentIndex)
}
