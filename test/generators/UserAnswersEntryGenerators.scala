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

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.identification.MovementReferenceNumberPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators {
  self: Generators =>

  implicit lazy val arbitraryIdentificationIdentificationNumberUserAnswersEntry: Arbitrary[(pages.identification.IdentificationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.IdentificationNumberPage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.IdentificationNumberPage, value)
    }

  implicit lazy val arbitraryIdentificationAuthorisationAuthorisationTypeUserAnswersEntry
    : Arbitrary[(pages.identification.authorisation.AuthorisationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.authorisation.AuthorisationTypePage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.authorisation.AuthorisationTypePage, value)
    }

  implicit lazy val arbitraryIdentificationAuthorisationAuthorisationReferenceNumberUserAnswersEntry
    : Arbitrary[(pages.identification.authorisation.AuthorisationReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.authorisation.AuthorisationReferenceNumberPage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.authorisation.AuthorisationReferenceNumberPage, value)
    }

  implicit lazy val arbitraryIdentificationAuthorisationAddAnotherAuthorisationUserAnswersEntry
    : Arbitrary[(pages.identification.authorisation.AddAnotherAuthorisationPage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.authorisation.AddAnotherAuthorisationPage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.authorisation.AddAnotherAuthorisationPage, value)
    }

  implicit lazy val arbitraryIdentificationIsSimplifiedProcedureUserAnswersEntry: Arbitrary[(pages.identification.IsSimplifiedProcedurePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.IsSimplifiedProcedurePage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.IsSimplifiedProcedurePage, value)
    }

  implicit lazy val arbitraryIdentificationArrivalDateUserAnswersEntry: Arbitrary[(pages.identification.ArrivalDatePage.type, JsValue)] =
    Arbitrary {
      for {
        value <- arbitrary[pages.identification.ArrivalDatePage.type#Data].map(Json.toJson(_))
      } yield (pages.identification.ArrivalDatePage, value)
    }

  implicit lazy val arbitraryMovementReferenceNumberUserAnswersEntry: Arbitrary[(MovementReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MovementReferenceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
