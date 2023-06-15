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

package generators

import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.equipment.itemNumber.ItemNumberDomain
import models.journeyDomain.incident.equipment.seal.SealDomain
import models.journeyDomain.incident.equipment.{EquipmentDomain, EquipmentsDomain}
import models.journeyDomain.incident.{IncidentDomain, IncidentsDomain}
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.{ArrivalDomain, ReaderError, UserAnswersReader}
import models.{EoriNumber, Index, MovementReferenceNumber, RichJsObject, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait UserAnswersGenerator extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryUserAnswers: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        mrn        <- arbitrary[MovementReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        answers    <- buildUserAnswers[ArrivalDomain](UserAnswers(mrn, eoriNumber))
      } yield answers
    }

  protected def buildUserAnswers[T](
    initialUserAnswers: UserAnswers
  )(implicit userAnswersReader: UserAnswersReader[T]): Gen[UserAnswers] = {

    def rec(userAnswers: UserAnswers): Gen[UserAnswers] =
      userAnswersReader.run(userAnswers) match {
        case Left(ReaderError(page, _)) =>
          generateAnswer
            .apply(page)
            .map {
              value =>
                userAnswers.copy(
                  data = userAnswers.data.setObject(page.path, value).getOrElse(userAnswers.data)
                )
            }
            .flatMap(rec)
        case Right(_) => Gen.const(userAnswers)
      }

    rec(initialUserAnswers)
  }

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

  def arbitraryItemNumberAnswers(userAnswers: UserAnswers, incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[ItemNumberDomain](userAnswers)(ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, itemNumberIndex))

  def arbitraryArrivalAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ArrivalDomain](userAnswers)
}
