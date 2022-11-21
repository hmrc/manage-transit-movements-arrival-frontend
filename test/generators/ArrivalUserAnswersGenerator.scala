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

package generators

import base.SpecBase
import models.journeyDomain.ArrivalDomain
import models.journeyDomain.identification.{AuthorisationDomain, AuthorisationsDomain, IdentificationDomain}
import models.journeyDomain.incident.equipment.{EquipmentDomain, EquipmentsDomain}
import models.journeyDomain.incident.seal.SealDomain
import models.journeyDomain.incident.{IncidentDomain, IncidentsDomain}
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import models.{Index, UserAnswers}
import org.scalacheck.Gen

trait ArrivalUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators with SpecBase =>

  def arbitraryAuthorisationsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[AuthorisationsDomain](userAnswers)

  def arbitraryAuthorisationAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[AuthorisationDomain](userAnswers)(AuthorisationDomain.userAnswersReader(index))

  def arbitraryIdentificationAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[IdentificationDomain](userAnswers)

  def arbitraryLocationOfGoodsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[LocationOfGoodsDomain](userAnswers)

  def arbitraryIncidentsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[IncidentsDomain](userAnswers)

  def arbitraryIncidentAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[IncidentDomain](userAnswers)(IncidentDomain.userAnswersReader(index))

  def arbitraryEquipmentAnswers(userAnswers: UserAnswers, incidentIndex: Index, equipmentIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[EquipmentDomain](userAnswers)(EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex))

  def arbitraryEquipmentsAnswers(userAnswers: UserAnswers, incidentIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[EquipmentsDomain](userAnswers)(EquipmentsDomain.userAnswersReader(incidentIndex))

  def arbitrarySealAnswers(userAnswers: UserAnswers, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[SealDomain](userAnswers)(SealDomain.userAnswersReader(incidentIndex, equipmentIndex, sealIndex))

  def arbitraryArrivalAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ArrivalDomain](userAnswers)(ArrivalDomain.userAnswersReader)

}
