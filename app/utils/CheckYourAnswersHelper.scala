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

package utils

import controllers.routes
import models.reference.CustomsOffice
import models.{Address, GoodsLocation, Mode, MovementReferenceNumber, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class CheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def consigneeEoriNumber: Option[SummaryListRow] = getAnswerAndBuildNamedRow[String](
    namePage = ConsigneeNamePage,
    answerPage = ConsigneeEoriNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "eoriNumber",
    id = Some("change-eori-number"),
    call = routes.ConsigneeEoriNumberController.onPageLoad(mrn, mode)
  )

  def consigneeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ConsigneeNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "consigneeName",
    id = Some("change-consignee-name"),
    call = routes.ConsigneeNameController.onPageLoad(mrn, mode)
  )

  def placeOfNotification: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = PlaceOfNotificationPage,
    formatAnswer = formatAsLiteral,
    prefix = "placeOfNotification",
    id = Some("change-place-of-notification"),
    call = routes.PlaceOfNotificationController.onPageLoad(mrn, mode)
  )

  def isTraderAddressPlaceOfNotification: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = IsTraderAddressPlaceOfNotificationPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "isTraderAddressPlaceOfNotification",
    id = Some("change-trader-address-place-of-notification"),
    call = routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(mrn, mode)
  )

  def incidentOnRoute: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = IncidentOnRoutePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "incidentOnRoute",
    id = Some("change-incident-on-route"),
    call = routes.IncidentOnRouteController.onPageLoad(mrn, mode)
  )

  def traderName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TraderNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderName",
    id = Some("change-trader-name"),
    call = routes.TraderNameController.onPageLoad(mrn, mode)
  )

  def traderEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TraderEoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderEori",
    id = Some("change-trader-eori"),
    call = routes.TraderEoriController.onPageLoad(mrn, mode)
  )

  def traderAddress: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = TraderAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderAddress",
    id = Some("change-trader-address"),
    call = routes.TraderAddressController.onPageLoad(mrn, mode)
  )

  def consigneeAddress: Option[SummaryListRow] = getAnswerAndBuildNamedRow[Address](
    namePage = ConsigneeNamePage,
    answerPage = ConsigneeAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "consigneeAddress",
    id = Some("change-consignee-address"),
    call = routes.ConsigneeAddressController.onPageLoad(mrn, mode)
  )

  def authorisedLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisedLocationPage,
    formatAnswer = formatAsLiteral,
    prefix = "authorisedLocation",
    id = Some("change-authorised-location"),
    call = routes.AuthorisedLocationController.onPageLoad(mrn, mode)
  )

  def customsSubPlace: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CustomsSubPlacePage,
    formatAnswer = formatAsLiteral,
    prefix = "customsSubPlace",
    id = Some("change-customs-sub-place"),
    call = routes.CustomsSubPlaceController.onPageLoad(mrn, mode)
  )

  def movementReferenceNumber: SummaryListRow = SummaryListRow(
    key = Key(messages("movementReferenceNumber.checkYourAnswersLabel").toText, "govuk-!-width-one-half"),
    value = Value(s"$mrn".toText)
  )

  def pickCustomsOffice: Option[SummaryListRow] =
    simplifiedCustomsOffice orElse customsOffice

  def simplifiedCustomsOffice: Option[SummaryListRow] =
    customsOffice(SimplifiedCustomsOfficePage, "customsOffice.simplified", routes.CustomsOfficeSimplifiedController.onPageLoad)

  def customsOffice: Option[SummaryListRow] =
    customsOffice(CustomsOfficePage, "customsOffice", routes.CustomsOfficeController.onPageLoad)

  def goodsLocation: Option[SummaryListRow] = getAnswerAndBuildRow[GoodsLocation](
    page = GoodsLocationPage,
    formatAnswer = goodsLocation => messages(s"goodsLocation.$goodsLocation").toText,
    prefix = "goodsLocation",
    id = Some("change-goods-location"),
    call = routes.GoodsLocationController.onPageLoad(mrn, mode)
  )

  private def customsOffice(
    page: QuestionPage[CustomsOffice],
    messageKeyPrefix: String,
    call: (MovementReferenceNumber, Mode) => Call
  ): Option[SummaryListRow] =
    userAnswers.get(page) flatMap {
      answer =>
        val location: Option[String] = userAnswers.get(CustomsSubPlacePage) orElse userAnswers.get(ConsigneeNamePage)

        location map {
          arg =>
            buildRow(
              prefix = messageKeyPrefix,
              answer = answer.toString.toText,
              id = Some("change-presentation-office"),
              call = call(mrn, mode),
              args = arg
            )
        }
    }
}
