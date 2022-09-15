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

package forms.mappings

import java.time.LocalDate
import models.reference.{Country, CustomsOffice, UnLocode}
import models.{CountryList, CustomsOfficeList, Enumerable, MovementReferenceNumber, UnLocodeList}
import play.api.data.FieldMapping
import play.api.data.Forms.of
import play.api.data.format.Formats.ignoredFormat

trait Mappings extends Formatters with Constraints {

  protected def mandatoryIfBoolean(condition: Boolean, requiredKey: String = "error.required", defaultResult: Boolean = true): FieldMapping[Boolean] =
    if (condition) boolean(requiredKey) else of(ignoredFormat(defaultResult))

  protected def trimmedText(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(trimmedStringFormatter(errorKey, args))

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric"
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def boolean(requiredKey: String = "error.required", invalidKey: String = "error.boolean", args: Seq[String] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A](requiredKey: String = "error.required", invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(invalidKey: String,
                          allRequiredKey: String,
                          twoRequiredKey: String,
                          requiredKey: String,
                          args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))

  protected def mrn(requiredKey: String, invalidKey: String, invalidCharacterKey: String): FieldMapping[MovementReferenceNumber] =
    of(mrnFormatter(requiredKey, invalidKey, invalidCharacterKey))

  protected def textWithSpacesRemoved(errorKey: String = "error.required"): FieldMapping[String] =
    of(spacelessStringFormatter(errorKey))

  protected def country(
    countryList: CountryList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[Country] =
    of(countryFormatter(countryList, errorKey, args))

  protected def customsOffice(
    customsOfficeList: CustomsOfficeList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[CustomsOffice] =
    of(customsOfficeFormatter(customsOfficeList, errorKey, args))

  protected def unLocode(
    unLocodeList: UnLocodeList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[UnLocode] =
    of(unLocodeFormatter(unLocodeList, errorKey, args))

}
