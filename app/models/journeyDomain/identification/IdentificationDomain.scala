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

package models.journeyDomain.identification

import cats.implicits._
import models.identification.ProcedureType
import models.journeyDomain.{EitherType, GettableAsReaderOps, UserAnswersReader}
import models.reference.CustomsOffice
import models.{MovementReferenceNumber, UserAnswers}
import pages.identification._

case class IdentificationDomain(
  mrn: MovementReferenceNumber,
  destinationOffice: CustomsOffice,
  identificationNumber: String,
  procedureType: ProcedureType,
  authorisations: Option[AuthorisationsDomain]
)

object IdentificationDomain {

  private val mrnReader: UserAnswersReader[MovementReferenceNumber] = {
    val fn: UserAnswers => EitherType[MovementReferenceNumber] = ua => Right(ua.mrn)
    UserAnswersReader(fn)
  }

  implicit val userAnswersReader: UserAnswersReader[IdentificationDomain] =
    for {
      mrn                  <- mrnReader
      destinationOffice    <- DestinationOfficePage.reader
      identificationNumber <- IdentificationNumberPage.reader
      isSimplified         <- IsSimplifiedProcedurePage.reader
      authorisations <- isSimplified match {
        case ProcedureType.Normal     => none[AuthorisationsDomain].pure[UserAnswersReader]
        case ProcedureType.Simplified => UserAnswersReader[AuthorisationsDomain].map(Some(_))
      }

    } yield IdentificationDomain(mrn, destinationOffice, identificationNumber, isSimplified, authorisations)
}
