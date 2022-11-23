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
import models.journeyDomain.incident.IncidentDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.{IncidentCodePage, IncidentFlagPage}
import pages.sections.incident.IncidentsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.AnswersHelper
import viewModels.ListItem

class IncidentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def incident(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[IncidentDomain](
    formatAnswer = _.asString(formatEnumAsString).toText,
    prefix = "incident.addAnotherIncident",
    id = Some(s"change-incident-${index.display}"),
    args = index.display
  )(IncidentDomain.userAnswersReader(index))

  def incidentFlag: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = IncidentFlagPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.incidentFlag",
    id = Option("change-incident-flag")
  )

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(IncidentsSection) {
      position =>
        val index = Index(position)
        buildListItemWithDefault[IncidentDomain, IncidentCode](
          page = IncidentCodePage(index),
          formatJourneyDomainModel = _.asString(formatEnumAsString),
          formatType = _.fold(messages("incident.label", index.display))(IncidentDomain.asString(index, _)(formatEnumAsString)),
          removeRoute = Option(Call(GET, "#"))
        )(IncidentDomain.userAnswersReader(index), implicitly)
    }
}

object IncidentsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) =
    new IncidentsAnswersHelper(userAnswers, mode)
}
