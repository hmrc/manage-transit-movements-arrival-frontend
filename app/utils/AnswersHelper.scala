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

import models.{MovementReferenceNumber, UserAnswers}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.{Content, SummaryListRow}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

class AnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) extends SummaryListRowHelper {

  def mrn: MovementReferenceNumber = userAnswers.movementReferenceNumber

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
    labelKey: String,
    id: Option[String],
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    userAnswers.get(page) map {
      answer =>
        buildSectionRow(
          prefix = prefix,
          labelKey = labelKey,
          answer = s"${formatAnswer(answer)}".toText,
          id = id,
          call = call,
          args = args: _*
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

}
