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

package models

import pages._
import pages.identification.DestinationOfficePage
import play.api.libs.json._
import queries.Gettable
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  mrn: MovementReferenceNumber,
  eoriNumber: EoriNumber,
  data: JsObject = Json.obj(),
  lastUpdated: LocalDateTime = LocalDateTime.now,
  arrivalId: Option[ArrivalId] = None,
  id: Id = Id()
) {

  def getOptional[A](page: Gettable[A])(implicit rds: Reads[A]): Either[String, Option[A]] =
    Reads
      .optionNoError(Reads.at(page.path))
      .reads(data)
      .asOpt
      .toRight(
        "Something went wrong"
      )

  def getAsEither[A](page: Gettable[A])(implicit rds: Reads[A]): Either[String, A] =
    Reads
      .optionNoError(Reads.at(page.path))
      .reads(data)
      .getOrElse(None)
      .toRight(
        "Something went wrong"
      )

  def get[A](gettable: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(gettable.path)).reads(data).getOrElse(None)

  def set[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A], reads: Reads[A]): Try[UserAnswers] = {
    lazy val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    lazy val cleanup: JsObject => Try[UserAnswers] = d => {
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }

    get(page) match {
      case Some(`value`) => Success(this)
      case _             => updatedData flatMap cleanup
    }
  }

  def remove[A](page: QuestionPage[A]): Try[UserAnswers] = {
    val updatedData    = data.removeObject(page.path).getOrElse(data)
    val updatedAnswers = copy(data = updatedData)
    page.cleanup(None, updatedAnswers)
  }

  def purge: UserAnswers =
    data.transform(DestinationOfficePage.path.json.pickBranch) match {
      case JsSuccess(value, _) => this.copy(data = value)
      case JsError(_)          => this.copy(data = Json.obj())
    }
}

object UserAnswers {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[UserAnswers] =
    (
      (__ \ "movementReferenceNumber").read[MovementReferenceNumber] and
        (__ \ "eoriNumber").read[EoriNumber] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.localDateTimeReads) and
        (__ \ "arrivalId").readNullable[ArrivalId] and
        (__ \ "_id").read[Id]
    )(UserAnswers.apply _)

  implicit lazy val writes: OWrites[UserAnswers] =
    (
      (__ \ "movementReferenceNumber").write[MovementReferenceNumber] and
        (__ \ "eoriNumber").write[EoriNumber] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.localDateTimeWrites) and
        (__ \ "arrivalId").writeNullable[ArrivalId] and
        (__ \ "_id").write[Id]
    )(unlift(UserAnswers.unapply))

  implicit lazy val format: Format[UserAnswers] = Format(reads, writes)
}
