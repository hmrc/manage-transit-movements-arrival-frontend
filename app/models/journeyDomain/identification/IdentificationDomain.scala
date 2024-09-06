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

package models.journeyDomain.identification

import models.identification.ProcedureType
import models.journeyDomain._
import models.reference.CustomsOffice
import models.{MovementReferenceNumber, UserAnswers}
import pages.identification._

case class IdentificationDomain(
  mrn: MovementReferenceNumber,
  destinationOffice: CustomsOffice,
  procedureType: ProcedureType,
  identificationNumber: String,
  authorisationReferenceNumber: Option[String]
) extends JourneyDomainModel

object IdentificationDomain {

  private val mrnReader: Read[MovementReferenceNumber] =
    UserAnswersReader.success {
      (ua: UserAnswers) => ua.mrn
    }

  implicit val userAnswersReader: Read[IdentificationDomain] =
    (mrnReader, DestinationOfficePage.reader, IsSimplifiedProcedurePage.reader, IdentificationNumberPage.reader)
      .to {
        case (mrn, destinationOffice, isSimplified, identificationNumber) =>
          val authorisationNumberReads: Read[Option[String]] = isSimplified match {
            case ProcedureType.Normal     => UserAnswersReader.none
            case ProcedureType.Simplified => AuthorisationReferenceNumberPage.reader.toOption
          }

          authorisationNumberReads.map(IdentificationDomain.apply(mrn, destinationOffice, isSimplified, identificationNumber, _))
      }
}
