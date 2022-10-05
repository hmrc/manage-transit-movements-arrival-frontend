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

import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.{JsString, JsValue, Json}
import queries.Gettable

import java.time.LocalDate

trait UserAnswersEntryGenerators {

  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateIdentificationAnswer

  private def generateIdentificationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.identification._
    generateAuthorisationAnswer orElse {
      case ArrivalDatePage           => arbitrary[LocalDate].map(Json.toJson(_))
      case IsSimplifiedProcedurePage => arbitrary[ProcedureType].map(Json.toJson(_))
      case IdentificationNumberPage  => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateAuthorisationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.identification.authorisation._
    {
      case AuthorisationTypePage(_)            => arbitrary[AuthorisationType].map(Json.toJson(_))
      case AuthorisationReferenceNumberPage(_) => Gen.alphaNumStr.map(JsString)
    }
  }
}
