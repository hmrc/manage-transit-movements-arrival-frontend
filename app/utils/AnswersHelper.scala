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

import models.journeyDomain.Stage.AccessingJourney
import models.journeyDomain.{JourneyDomainModel, ReaderError, UserAnswersReader}
import models.{Index, Mode, MovementReferenceNumber, RichJsArray, RichOptionJsArray, UserAnswers}
import navigation.UserAnswersNavigator
import pages.QuestionPage
import pages.sections.Section
import play.api.i18n.Messages
import play.api.libs.json.{JsArray, Reads}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.{Content, SummaryListRow}
import viewModels.{Link, ListItem, ListItemWithSuffixHiddenArg, ParentListItem}

class AnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends SummaryListRowHelper {

  def mrn: MovementReferenceNumber = userAnswers.mrn

  protected def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    args: Any*
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    for {
      answer <- userAnswers.get(page)
      call   <- page.route(userAnswers, mode)
    } yield buildRow(
      prefix = prefix,
      answer = formatAnswer(answer),
      id = id,
      call = call,
      args = args: _*
    )

  def getAnswersAndBuildSectionRows(section: Section[JsArray])(f: Index => Option[SummaryListRow]): Seq[SummaryListRow] =
    userAnswers
      .get(section)
      .mapWithIndex {
        (_, index) => f(index)
      }

  def getAnswerAndBuildSectionRow[A <: JourneyDomainModel](
    formatAnswer: A => Content,
    prefix: String,
    id: Option[String],
    args: Any*
  )(implicit userAnswersReader: UserAnswersReader[A]): Option[SummaryListRow] =
    userAnswersReader
      .run(userAnswers)
      .map(
        x =>
          buildSimpleRow(
            prefix = prefix,
            label = messages(s"$prefix.label", args: _*),
            answer = formatAnswer(x),
            id = id,
            call = Some(UserAnswersNavigator.nextPage[A](userAnswers, mode, AccessingJourney)),
            args = args: _*
          )
      )
      .toOption

  protected def buildListItems(
    section: Section[JsArray]
  )(block: Index => Option[Either[ParentListItem, ParentListItem]]): Seq[Either[ParentListItem, ParentListItem]] =
    userAnswers
      .get(section)
      .mapWithIndex {
        (_, index) => block(index)
      }

  protected def buildListItemWithSuffixHiddenArg[A <: JourneyDomainModel, B](
    page: QuestionPage[B],
    formatJourneyDomainModel: A => String,
    formatType: B => String,
    removeRoute: Option[Call],
    hiddenArg: String
  )(implicit userAnswersReader: UserAnswersReader[A], rds: Reads[B]): Option[Either[ParentListItem, ParentListItem]] =
    buildListItemWithSuffixHiddenArg(
      formatJourneyDomainModel,
      removeRoute,
      hiddenArg
    ) {
      _.page.route(userAnswers, mode).flatMap {
        changeRoute =>
          userAnswers
            .get(page)
            .map {
              value =>
                ListItemWithSuffixHiddenArg(
                  name = formatType(value),
                  changeUrl = changeRoute.url,
                  removeUrl = removeRoute.map(_.url),
                  hiddenArg
                )
            }
            .map(Left(_))
      }
    }

  protected def buildListItem[A <: JourneyDomainModel, B](
    page: QuestionPage[B],
    formatJourneyDomainModel: A => String,
    formatType: B => String,
    removeRoute: Option[Call]
  )(implicit userAnswersReader: UserAnswersReader[A], rds: Reads[B]): Option[Either[ParentListItem, ParentListItem]] =
    buildListItem(
      formatJourneyDomainModel,
      removeRoute
    ) {
      _.page.route(userAnswers, mode).flatMap {
        changeRoute =>
          userAnswers
            .get(page)
            .map {
              value =>
                ListItem(
                  name = formatType(value),
                  changeUrl = changeRoute.url,
                  removeUrl = removeRoute.map(_.url)
                )
            }
            .map(Left(_))
      }
    }

  protected def buildListItemWithDefault[A <: JourneyDomainModel, B](
    page: QuestionPage[B],
    formatJourneyDomainModel: A => String,
    formatType: Option[B] => String,
    removeRoute: Option[Call]
  )(implicit userAnswersReader: UserAnswersReader[A], rds: Reads[B]): Option[Either[ParentListItem, ParentListItem]] =
    buildListItem(
      formatJourneyDomainModel,
      removeRoute
    ) {
      _.page.route(userAnswers, mode).map {
        changeRoute =>
          Left(
            ListItem(
              name = formatType(userAnswers.get(page)),
              changeUrl = changeRoute.url,
              removeUrl = removeRoute.map(_.url)
            )
          )
      }
    }

  private def buildListItemWithSuffixHiddenArg[A <: JourneyDomainModel, B](
    formatJourneyDomainModel: A => String,
    removeRoute: Option[Call],
    hiddenArg: String
  )(
    f: ReaderError => Option[Either[ListItemWithSuffixHiddenArg, ListItemWithSuffixHiddenArg]]
  )(implicit userAnswersReader: UserAnswersReader[A]): Option[Either[ParentListItem, ParentListItem]] =
    userAnswersReader.run(userAnswers) match {
      case Left(readerError) =>
        f(readerError)
      case Right(journeyDomainModel) =>
        journeyDomainModel.routeIfCompleted(userAnswers, mode, AccessingJourney).map {
          changeRoute =>
            Right(
              ListItemWithSuffixHiddenArg(
                name = formatJourneyDomainModel(journeyDomainModel),
                changeUrl = changeRoute.url,
                removeUrl = removeRoute.map(_.url),
                hiddenArg
              )
            )
        }
    }

  private def buildListItem[A <: JourneyDomainModel, B](
    formatJourneyDomainModel: A => String,
    removeRoute: Option[Call]
  )(f: ReaderError => Option[Either[ListItem, ListItem]])(implicit userAnswersReader: UserAnswersReader[A]): Option[Either[ParentListItem, ParentListItem]] =
    userAnswersReader.run(userAnswers) match {
      case Left(readerError) =>
        f(readerError)
      case Right(journeyDomainModel) =>
        journeyDomainModel.routeIfCompleted(userAnswers, mode, AccessingJourney).map {
          changeRoute =>
            Right(
              ListItem(
                name = formatJourneyDomainModel(journeyDomainModel),
                changeUrl = changeRoute.url,
                removeUrl = removeRoute.map(_.url)
              )
            )
        }
    }

  protected def buildLink(section: Section[JsArray])(link: => Link): Option[Link] =
    if (userAnswers.get(section).exists(_.nonEmpty)) Some(link) else None
}
