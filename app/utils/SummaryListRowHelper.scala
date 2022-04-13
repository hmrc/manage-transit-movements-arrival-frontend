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

import models.reference.CountryCode
import models.{Address, CountryList, MovementReferenceNumber, UserAnswers}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

private[utils] class SummaryListRowHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def mrn: MovementReferenceNumber = userAnswers.movementReferenceNumber

  def formatAsYesOrNo(answer: Boolean): Content =
    if (answer) {
      messages("site.yes").toText
    } else {
      messages("site.no").toText
    }

  def formatAsAddress(address: Address): Content =
    HtmlContent(Seq(address.buildingAndStreet, address.city, address.postcode).mkString("<br>"))

  def formatAsLiteral[T](answer: T): Content = s"$answer".toText

  def formatAsCountry(countryList: CountryList)(answer: CountryCode): Content =
    s"${countryList.getCountry(answer).map(_.description).getOrElse(answer.code)}".toText

  def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    userAnswers.get(page) map {
      answer =>
        buildRow(
          prefix = prefix,
          answer = formatAnswer(answer),
          id = id,
          call = call,
          args = args: _*
        )
    }

  def getAnswerAndBuildNamedRow[T](
    namePage: QuestionPage[String],
    answerPage: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    call: Call
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    userAnswers.get(namePage) flatMap {
      name =>
        getAnswerAndBuildRow[T](
          page = answerPage,
          formatAnswer = formatAnswer,
          prefix = prefix,
          id = id,
          call = call,
          args = name
        )
    }

  def getAnswerAndBuildSectionRow[T](
    page: QuestionPage[T],
    formatAnswer: T => String,
    prefix: String,
    label: Content,
    id: Option[String],
    call: Call
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    userAnswers.get(page) map {
      answer =>
        buildSimpleRow(
          prefix = prefix,
          label = label,
          answer = s"${formatAnswer(answer)}".toText,
          id = id,
          call = call,
          args = formatAnswer(answer)
        )
    }

  def getAnswerAndBuildListItem[T](
    page: QuestionPage[T],
    formatAnswer: T => String,
    changeCall: Call,
    removeCall: Call
  )(implicit rds: Reads[T]): Option[ListItem] =
    userAnswers.get(page) map {
      answer =>
        ListItem(
          name = formatAnswer(answer),
          changeUrl = changeCall.url,
          removeUrl = removeCall.url
        )
    }

  def buildRow(
    prefix: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    buildSimpleRow(
      prefix = prefix,
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*).toText,
      answer = answer,
      id = id,
      call = call,
      args = args: _*
    )

  def buildSimpleRow(
    prefix: String,
    label: Content,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    SummaryListRow(
      key = Key(label, classes = "govuk-!-width-one-half"),
      value = Value(answer),
      actions = Some(
        Actions(items =
          List(
            ActionItem(
              content = messages("site.edit").toText,
              href = call.url,
              visuallyHiddenText = Some(messages(s"$prefix.change.hidden", args: _*)),
              attributes = id.fold[Map[String, String]](Map.empty)(
                id => Map("id" -> id)
              )
            )
          )
        )
      )
    )

}
