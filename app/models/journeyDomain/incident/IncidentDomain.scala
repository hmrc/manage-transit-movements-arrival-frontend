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

package models.journeyDomain.incident

import config.Constants.IncidentCode._
import models.journeyDomain.incident.endorsement.EndorsementDomain
import models.journeyDomain.incident.equipment.EquipmentsDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, Read, Stage}
import models.reference.IncidentCode._
import models.reference.{Country, IncidentCode}
import models.{Index, Mode, UserAnswers}
import pages.incident.{AddEndorsementPage, IncidentCodePage, IncidentCountryPage, IncidentTextPage}
import play.api.i18n.Messages
import play.api.mvc.Call

case class IncidentDomain(
  incidentCountry: Country,
  incidentCode: IncidentCode,
  incidentText: String,
  endorsement: Option[EndorsementDomain],
  location: IncidentLocationDomain,
  equipments: EquipmentsDomain,
  transportMeans: Option[TransportMeansDomain]
)(index: Index)
    extends JourneyDomainModel {

  def asString()(implicit messages: Messages): String =
    IncidentDomain.asString(index, incidentCode)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.incident.routes.CheckIncidentAnswersController.onPageLoad(userAnswers.mrn, mode, index))
}

object IncidentDomain {

  def asString(index: Index, incidentCode: IncidentCode)(implicit messages: Messages): String =
    messages("incident.value", index.display, incidentCode.description)

  def userAnswersReader(index: Index): Read[IncidentDomain] = {

    val transportMeansReads: Read[Option[TransportMeansDomain]] =
      IncidentCodePage(index)
        .filterOptionalDependent(
          x => x.code == TransferredToAnotherTransportCode || x.code == UnexpectedlyChangedCode
        )(TransportMeansDomain.userAnswersReader(index))

    (
      IncidentCountryPage(index).reader,
      IncidentCodePage(index).reader,
      IncidentTextPage(index).reader,
      AddEndorsementPage(index).filterOptionalDependent(identity)(EndorsementDomain.userAnswersReader(index)),
      IncidentLocationDomain.userAnswersReader(index),
      EquipmentsDomain.userAnswersReader(index),
      transportMeansReads
    ).map(IncidentDomain.apply(_, _, _, _, _, _, _)(index))
  }

}
