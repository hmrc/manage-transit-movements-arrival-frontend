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
import models.incident.IncidentCode
import models.locationOfGoods.TypeOfLocation
import models.reference.{Country, CustomsOffice, UnLocode}
import models.{Coordinates, DynamicAddress, InternationalAddress, PostalCodeAddress, QualifierOfIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import queries.Gettable

import java.time.LocalDate

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateIdentificationAnswer orElse
      generateLocationOfGoodsAnswer orElse
      generateIncidentAnswer

  private def generateIdentificationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.identification._
    generateAuthorisationAnswer orElse {
      case DestinationOfficePage     => arbitrary[CustomsOffice].map(Json.toJson(_))
      case IdentificationNumberPage  => Gen.alphaNumStr.map(JsString)
      case IsSimplifiedProcedurePage => arbitrary[ProcedureType].map(Json.toJson(_))
    }
  }

  private def generateAuthorisationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.identification.authorisation._
    {
      case AuthorisationTypePage(_)            => arbitrary[AuthorisationType].map(Json.toJson(_))
      case AuthorisationReferenceNumberPage(_) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateLocationOfGoodsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.locationOfGoods._
    {
      val log: PartialFunction[Gettable[_], Gen[JsValue]] = {
        case TypeOfLocationPage            => arbitrary[TypeOfLocation].map(Json.toJson(_))
        case QualifierOfIdentificationPage => arbitrary[QualifierOfIdentification].map(Json.toJson(_))
        case AddContactPersonPage          => arbitrary[Boolean].map(JsBoolean)
      }

      log orElse
        generateLocationOfGoodsIdentifierAnswer orElse
        generateLocationOfGoodsContactAnswer
    }
  }

  private def generateLocationOfGoodsIdentifierAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.locationOfGoods._
    {
      case AddAdditionalIdentifierPage => arbitrary[Boolean].map(JsBoolean)
      case AdditionalIdentifierPage    => Gen.alphaNumStr.map(JsString)
      case AddressPage                 => arbitrary[PostalCodeAddress].map(Json.toJson(_))
      case AuthorisationNumberPage     => Gen.alphaNumStr.map(JsString)
      case CoordinatesPage             => arbitrary[Coordinates].map(Json.toJson(_))
      case CustomsOfficePage           => arbitrary[CustomsOffice].map(Json.toJson(_))
      case IdentificationNumberPage    => Gen.alphaNumStr.map(JsString)
      case InternationalAddressPage    => arbitrary[InternationalAddress].map(Json.toJson(_))
      case UnlocodePage                => arbitrary[UnLocode].map(Json.toJson(_))
    }
  }

  private def generateLocationOfGoodsContactAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.locationOfGoods._
    {
      case ContactPersonNamePage      => Gen.alphaNumStr.map(JsString)
      case ContactPersonTelephonePage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateIncidentAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.incident._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case IncidentFlagPage               => arbitrary[Boolean].map(JsBoolean)
      case IncidentCountryPage(_)         => arbitrary[Country].map(Json.toJson(_))
      case IncidentCodePage(_)            => arbitrary[IncidentCode].map(Json.toJson(_))
      case IncidentTextPage(_)            => Gen.alphaNumStr.map(JsString)
      case AddEndorsementPage(_)          => arbitrary[Boolean].map(JsBoolean)
      case ContainerIndicatorYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case AddTransportEquipmentPage(_)   => arbitrary[Boolean].map(JsBoolean)
    }

    pf orElse
      generateIncidentEndorsementAnswer orElse
      generateIncidentLocationAnswer orElse
      generateIncidentEquipmentAnswer orElse
      generateIncidentEquipmentSeal
  }

  private def generateIncidentEndorsementAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.incident._
    {
      case EndorsementDatePage(_)      => arbitrary[LocalDate].map(Json.toJson(_))
      case EndorsementAuthorityPage(_) => Gen.alphaNumStr.map(JsString)
      case EndorsementLocationPage(_)  => Gen.alphaNumStr.map(JsString)
      case EndorsementCountryPage(_)   => arbitrary[Country].map(Json.toJson(_))
    }
  }

  private def generateIncidentLocationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.incident.location._
    {
      case QualifierOfIdentificationPage(_) => arbitrary[QualifierOfIdentification].map(Json.toJson(_))
      case CoordinatesPage(_)               => arbitrary[Coordinates].map(Json.toJson(_))
      case UnLocodePage(_)                  => arbitrary[UnLocode].map(Json.toJson(_))
      case AddressPage(_)                   => arbitrary[DynamicAddress].map(Json.toJson(_))
    }
  }

  private def generateIncidentEquipmentAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.incident.equipment._
    {
      case ContainerIdentificationNumberYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case ContainerIdentificationNumberPage(_, _)      => Gen.alphaNumStr.map(JsString)
      case AddSealsYesNoPage(_, _)                      => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateIncidentEquipmentSeal: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.incident.equipment.seal._
    {
      case SealIdentificationNumberPage(_, _, _) => Gen.alphaNumStr.map(JsString)
    }
  }

}
