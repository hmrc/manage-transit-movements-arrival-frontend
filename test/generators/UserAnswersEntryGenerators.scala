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

import models.identification.ProcedureType
import models.reference.*
import models.{Coordinates, DynamicAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import queries.Gettable

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[?], Gen[JsValue]] =
    generateIdentificationAnswer orElse
      generateLocationOfGoodsAnswer

  private def generateIdentificationAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.identification.*
    {
      case DestinationOfficePage            => arbitrary[CustomsOffice].map(Json.toJson(_))
      case IdentificationNumberPage         => Gen.alphaNumStr.map(JsString.apply)
      case IsSimplifiedProcedurePage        => arbitrary[ProcedureType].map(Json.toJson(_))
      case AuthorisationReferenceNumberPage => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateLocationOfGoodsAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.locationOfGoods.*
    {
      val log: PartialFunction[Gettable[?], Gen[JsValue]] = {
        case TypeOfLocationPage            => arbitrary[TypeOfLocation].map(Json.toJson(_))
        case QualifierOfIdentificationPage => arbitrary[QualifierOfIdentification].map(Json.toJson(_))
        case AddContactPersonPage          => arbitrary[Boolean].map(JsBoolean)
      }

      log orElse
        generateLocationOfGoodsIdentifierAnswer orElse
        generateLocationOfGoodsContactAnswer
    }
  }

  private def generateLocationOfGoodsIdentifierAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.locationOfGoods.*
    {
      case AuthorisationNumberPage  => Gen.alphaNumStr.map(JsString.apply)
      case CoordinatesPage          => arbitrary[Coordinates].map(Json.toJson(_))
      case CustomsOfficePage        => arbitrary[CustomsOffice].map(Json.toJson(_))
      case IdentificationNumberPage => Gen.alphaNumStr.map(JsString.apply)
      case CountryPage              => arbitrary[Country].map(Json.toJson(_))
      case AddressPage              => arbitrary[DynamicAddress].map(Json.toJson(_))
      case UnlocodePage             => arbitrary[String].map(Json.toJson(_))
    }
  }

  private def generateLocationOfGoodsContactAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.locationOfGoods.*
    {
      case ContactPersonNamePage      => Gen.alphaNumStr.map(JsString.apply)
      case ContactPersonTelephonePage => Gen.alphaNumStr.map(JsString.apply)
    }
  }
}
