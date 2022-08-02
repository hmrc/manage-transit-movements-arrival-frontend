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
import models.{Address, CountryList}
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private[utils] class SummaryListRowHelper(implicit messages: Messages) {

  def formatAsYesOrNo(answer: Boolean): Content =
    if (answer) {
      messages("site.yes").toText
    } else {
      messages("site.no").toText
    }

  def formatAsAddress(address: Address): Content =
    HtmlContent(Seq(address.line1, address.line2, address.postalCode).mkString("<br>"))

  def formatAsText[T](answer: T): Content = s"$answer".toText

  def formatAsDate(answer: LocalDate): Content = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    answer.format(formatter).toText
  }

  def formatAsCountry(countryList: CountryList)(answer: CountryCode): Content =
    s"${countryList.getCountry(answer).map(_.description).getOrElse(answer.code)}".toText

  protected def formatEnumAsText[T](messageKeyPrefix: String)(answer: T): Content =
    formatEnumAsString(messageKeyPrefix)(answer).toText

  protected def formatEnumAsString[T](messageKeyPrefix: String)(answer: T): String =
    messages(s"$messageKeyPrefix.$answer")

  def buildRow(
    prefix: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    buildSimpleRow(
      prefix = prefix,
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*),
      answer = answer,
      id = id,
      call = call,
      args = args: _*
    )

  def buildSimpleRow(
    prefix: String,
    label: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    SummaryListRow(
      key = label.toKey,
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
