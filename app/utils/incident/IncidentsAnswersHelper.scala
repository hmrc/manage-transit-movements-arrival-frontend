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

import controllers.incident.routes
import models.incident.IncidentCode
import models.journeyDomain.incident.IncidentDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.{IncidentCodePage, IncidentFlagPage}
import pages.sections.incident.IncidentsSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper
import viewModels.{Link, ListItem}

class IncidentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def incidents: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(IncidentsSection)(incident)

  def incident(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[IncidentDomain](
    formatAnswer = _.asString().toText,
    prefix = "incident",
    id = Some(s"change-incident-${index.display}"),
    args = index.display
  )(IncidentDomain.userAnswersReader(index))

  def addOrRemoveIncidents(): Option[Link] = buildLink(IncidentsSection) {
    Link(
      id = "add-or-remove-incidents",
      text = messages("arrivals.checkYourAnswers.incidents.addOrRemove"),
      href = controllers.incident.routes.AddAnotherIncidentController.onPageLoad(userAnswers.mrn, mode).url
    )
  }

  def incidentFlag: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = IncidentFlagPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "incident.incidentFlag",
    id = Option("change-incident-flag")
  )

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(IncidentsSection) {
      index =>
        buildListItemWithDefault[IncidentDomain, IncidentCode](
          page = IncidentCodePage(index),
          formatJourneyDomainModel = _.asString(),
          formatType = _.fold(messages("incident.label", index.display))(IncidentDomain.asString(index, _)),
          removeRoute = Option(routes.ConfirmRemoveIncidentController.onPageLoad(mrn, mode, index))
        )(IncidentDomain.userAnswersReader(index), implicitly)
    }
}

object IncidentsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) =
    new IncidentsAnswersHelper(userAnswers, mode)
}
