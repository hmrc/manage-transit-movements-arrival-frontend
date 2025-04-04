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

package models

import models.domain.StringFieldRegex._
import play.api.libs.json._
import play.api.mvc.{JavascriptLiteral, PathBindable}

import scala.util.matching.Regex

final case class MovementReferenceNumber(value: String) {

  override def toString: String = value
}

object MovementReferenceNumber {

  object Constants {
    val length               = 18
    val validCharactersRegex = """^[a-zA-Z0-9 ]*$"""
  }

  def apply(input: String): Option[MovementReferenceNumber] =
    validate(input, mrnTransitionRegex) orElse
      validate(input, mrnFinalRegex)

  def validate(input: String): Option[MovementReferenceNumber] =
    validate(input, mrnFinalRegex)

  private def validate(input: String, regex: Regex): Option[MovementReferenceNumber] =
    input match {
      case regex(year, countryCode, serial, checkCharacter) =>
        if (isCheckCharacterValid(year, countryCode, serial, checkCharacter)) {
          Some(new MovementReferenceNumber(input))
        } else {
          None
        }
      case _ =>
        None
    }

  private def isCheckCharacterValid(year: String, countryCode: String, serial: String, checkCharacter: String): Boolean =
    getCheckCharacter(year, countryCode, serial) == checkCharacter

  /** @param year
    *   year in 2-digit form (e.g. 24 = 2024)
    * @param countryCode
    *   country code (e.g. FR = France)
    * @param serial
    *   serial number made up of 13 alphanumeric characters
    * @return
    *   the check character following these steps:
    *   1. Assign a numerical value to each letter of the alphabet, beginning with 10 for the letter A (multiples of 11 are omitted, hence B is 12)
    *   1. Concatenate the year, country code and serial number and determine the numerical value for each character
    *   1. Multiply each number by 2^position^, where position is the index of the character in the string (starting from 0)
    *   1. Add all of the results together
    *   1. Divide the result by 11 and find the remainder
    *      - If the remainder is 0-9, return the remainder
    *      - If the remainder is 10, return 0
    */
  def getCheckCharacter(year: String, countryCode: String, serial: String): String = {
    val input = s"$year$countryCode$serial"

    val multiplicationFactors = input.zipWithIndex.map {
      case (character, index) =>
        characterWeights.apply(character) * Math.pow(2, index).toInt
    }

    val remainder = multiplicationFactors.sum % 11

    (remainder % 10).toString
  }

  private val characterWeights = Map(
    '0' -> 0,
    '1' -> 1,
    '2' -> 2,
    '3' -> 3,
    '4' -> 4,
    '5' -> 5,
    '6' -> 6,
    '7' -> 7,
    '8' -> 8,
    '9' -> 9,
    'A' -> 10,
    'B' -> 12,
    'C' -> 13,
    'D' -> 14,
    'E' -> 15,
    'F' -> 16,
    'G' -> 17,
    'H' -> 18,
    'I' -> 19,
    'J' -> 20,
    'K' -> 21,
    'L' -> 23,
    'M' -> 24,
    'N' -> 25,
    'O' -> 26,
    'P' -> 27,
    'Q' -> 28,
    'R' -> 29,
    'S' -> 30,
    'T' -> 31,
    'U' -> 32,
    'V' -> 34,
    'W' -> 35,
    'X' -> 36,
    'Y' -> 37,
    'Z' -> 38
  )

  implicit lazy val reads: Reads[MovementReferenceNumber] =
    __.read[String].map(MovementReferenceNumber.apply).flatMap {
      case Some(mrn) =>
        Reads(
          _ => JsSuccess(mrn)
        )
      case None =>
        Reads(
          _ => JsError("Invalid Movement Reference Number")
        )
    }

  implicit lazy val writes: Writes[MovementReferenceNumber] = Writes {
    mrn => JsString(mrn.toString)
  }

  implicit def pathBindable: PathBindable[MovementReferenceNumber] = new PathBindable[MovementReferenceNumber] {

    override def bind(key: String, value: String): Either[String, MovementReferenceNumber] =
      MovementReferenceNumber.apply(value).toRight("Invalid Movement Reference Number")

    override def unbind(key: String, value: MovementReferenceNumber): String =
      value.toString
  }

  implicit val mrnJSLBinder: JavascriptLiteral[MovementReferenceNumber] = (value: MovementReferenceNumber) => s"""'${value.toString}'"""
}
