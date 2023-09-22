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

package utils

import models.locationOfGoods.TypeOfLocation
import models.reference.{Country, CustomsOffice, QualifierOfIdentification}
import models.{Coordinates, DynamicAddress, Mode, PostalCodeAddress, UserAnswers}
import pages.locationOfGoods._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

class LocationOfGoodsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def locationType: Option[SummaryListRow] = getAnswerAndBuildRow[TypeOfLocation](
    page = TypeOfLocationPage,
    formatAnswer = formatEnumAsText(TypeOfLocation.messageKeyPrefix),
    prefix = "locationOfGoods.typeOfLocation",
    id = Some("type-of-location")
  )

  def qualifierOfIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[QualifierOfIdentification](
    page = QualifierOfIdentificationPage,
    formatAnswer = formatDynamicEnumAsText(_),
    prefix = "locationOfGoods.qualifierOfIdentification",
    id = Some("qualifier-of-identification")
  )

  def customsOfficeIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = CustomsOfficePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.customsOffice",
    id = Some("customs-office")
  )

  def identificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.identificationNumber",
    id = Some("identification-number")
  )

  def authorisationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisationNumberPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.authorisationNumber",
    id = Some("authorisation-number")
  )

  def coordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = CoordinatesPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.coordinates",
    id = Some("coordinates")
  )

  def unLocode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UnlocodePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.unlocode",
    id = Some("un-locode")
  )

  def country: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.country",
    id = Some("country")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "locationOfGoods.address",
    id = Some("address")
  )

  def postalCode: Option[SummaryListRow] = getAnswerAndBuildRow[PostalCodeAddress](
    page = PostalCodePage,
    formatAnswer = formatAsPostalCodeAddress,
    prefix = "locationOfGoods.postalCode",
    id = Some("postal-code")
  )

  def contactYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPersonPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "locationOfGoods.addContactPerson",
    id = Some("add-contact-person")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContactPersonNamePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.contactPersonName",
    id = Some("contact-person-name")
  )

  def contactPhoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContactPersonTelephonePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.contactPersonTelephone",
    id = Some("contact-person-phone")
  )

}

object LocationOfGoodsAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode
  )(implicit messages: Messages): LocationOfGoodsAnswersHelper =
    new LocationOfGoodsAnswersHelper(userAnswers, mode)
}
