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
import models.journeyDomain.{EitherType, GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.{MovementReferenceNumber, UserAnswers}
import pages.identification.{IdentificationNumberPage, _}
import play.api.mvc.Call

import java.time.LocalDate

case class IdentificationDomain(
  mrn: MovementReferenceNumber,
  procedureType: ProcedureType,
  authorisations: AuthorisationsDomain,
  identificationNumber: String
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.identification.routes.CheckIdentificationAnswersController.onPageLoad(userAnswers.mrn))
}

object IdentificationDomain {

  private val mrn: UserAnswersReader[MovementReferenceNumber] = {
    val fn: UserAnswers => EitherType[MovementReferenceNumber] = ua => Right(ua.mrn)
    UserAnswersReader(fn)
  }

  implicit val userAnswersReader: UserAnswersReader[IdentificationDomain] =
    (
      mrn,
      IsSimplifiedProcedurePage.reader,
      UserAnswersReader[AuthorisationsDomain],
      IdentificationNumberPage.reader
    ).tupled.map((IdentificationDomain.apply _).tupled)
}
