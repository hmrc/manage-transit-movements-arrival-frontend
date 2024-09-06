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
import models.reference._
import models.{Coordinates, DynamicAddress, PostalCodeAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import queries.Gettable

import java.time.LocalDate

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[?], Gen[JsValue]] =
    generateIdentificationAnswer orElse
      generateLocationOfGoodsAnswer orElse
      generateIncidentAnswer

  private def generateIdentificationAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.identification._
    {
      case DestinationOfficePage            => arbitrary[CustomsOffice].map(Json.toJson(_))
      case IdentificationNumberPage         => Gen.alphaNumStr.map(JsString.apply)
      case IsSimplifiedProcedurePage        => arbitrary[ProcedureType].map(Json.toJson(_))
      case AuthorisationReferenceNumberPage => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateLocationOfGoodsAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.locationOfGoods._
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
    import pages.locationOfGoods._
    {
      case PostalCodePage           => arbitrary[PostalCodeAddress].map(Json.toJson(_))
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
    import pages.locationOfGoods._
    {
      case ContactPersonNamePage      => Gen.alphaNumStr.map(JsString.apply)
      case ContactPersonTelephonePage => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateIncidentAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident._
    val pf: PartialFunction[Gettable[?], Gen[JsValue]] = {
      case IncidentFlagPage               => arbitrary[Boolean].map(JsBoolean)
      case IncidentCountryPage(_)         => arbitrary[Country].map(Json.toJson(_))
      case IncidentCodePage(_)            => arbitrary[IncidentCode].map(Json.toJson(_))
      case IncidentTextPage(_)            => Gen.alphaNumStr.map(JsString.apply)
      case AddEndorsementPage(_)          => arbitrary[Boolean].map(JsBoolean)
      case ContainerIndicatorYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case AddTransportEquipmentPage(_)   => arbitrary[Boolean].map(JsBoolean)
    }

    pf orElse
      generateIncidentEndorsementAnswer orElse
      generateIncidentLocationAnswer orElse
      generateIncidentEquipmentAnswer orElse
      generateIncidentEquipmentSealAnswer orElse
      generateIncidentEquipmentItemNumberAnswer orElse
      generateIncidentTransportMeansAnswer
  }

  private def generateIncidentEndorsementAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.endorsement._
    {
      case EndorsementDatePage(_)      => arbitrary[LocalDate].map(Json.toJson(_))
      case EndorsementAuthorityPage(_) => Gen.alphaNumStr.map(JsString.apply)
      case EndorsementLocationPage(_)  => Gen.alphaNumStr.map(JsString.apply)
      case EndorsementCountryPage(_)   => arbitrary[Country].map(Json.toJson(_))
    }
  }

  private def generateIncidentLocationAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.location._
    {
      case QualifierOfIdentificationPage(_) => arbitrary[QualifierOfIdentification].map(Json.toJson(_))
      case CoordinatesPage(_)               => arbitrary[Coordinates].map(Json.toJson(_))
      case UnLocodePage(_)                  => arbitrary[String].map(Json.toJson(_))
      case AddressPage(_)                   => arbitrary[DynamicAddress].map(Json.toJson(_))
    }
  }

  private def generateIncidentEquipmentAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.equipment._
    {
      case ContainerIdentificationNumberYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case ContainerIdentificationNumberPage(_, _)      => Gen.alphaNumStr.map(JsString.apply)
      case AddSealsYesNoPage(_, _)                      => arbitrary[Boolean].map(JsBoolean)
      case AddGoodsItemNumberYesNoPage(_, _)            => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateIncidentEquipmentSealAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.equipment.seal._
    {
      case SealIdentificationNumberPage(_, _, _) => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateIncidentEquipmentItemNumberAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.equipment.itemNumber._
    {
      case ItemNumberPage(_, _, _) => Gen.numStr.map(JsString.apply)
    }
  }

  private def generateIncidentTransportMeansAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.incident.transportMeans._
    {
      case IdentificationPage(_)       => arbitrary[Identification].map(Json.toJson(_))
      case IdentificationNumberPage(_) => Gen.alphaNumStr.map(JsString.apply)
      case TransportNationalityPage(_) => arbitrary[Nationality].map(Json.toJson(_))
    }
  }

}
