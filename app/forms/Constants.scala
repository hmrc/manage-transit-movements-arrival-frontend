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

package forms

trait FormConstants {
  val addiationalIdentifierMaxLength: Int
  val tirCarnetReferenceMaxLength: Int
  val maxEoriNumberLength: Int
  val minEoriNumberLength: Int
  val maxNameLength: Int
  val maxTelephoneNumberLength: Int
  val minTelephoneNumberLength: Int
  val authorisationNumberLength: Int
  val maxIncidentTextLength: Int
}

class TransitionConstants extends FormConstants {
  val addiationalIdentifierMaxLength: Int = 4
  val tirCarnetReferenceMaxLength: Int    = 12
  val maxEoriNumberLength: Int            = 17
  val minEoriNumberLength: Int            = 14
  val maxNameLength: Int                  = 70
  val maxTelephoneNumberLength: Int       = 35
  val minTelephoneNumberLength: Int       = 6
  val maxIncidentTextLength: Int          = 512

  // DEMO of changing constants (not really constants anymore, might change name)
  val authorisationNumberLength: Int      = 2
}

class PostTransitionConstants extends FormConstants {
  val addiationalIdentifierMaxLength: Int = 4
  val tirCarnetReferenceMaxLength: Int    = 12
  val maxEoriNumberLength: Int            = 17
  val minEoriNumberLength: Int            = 14
  val maxNameLength: Int                  = 70
  val maxTelephoneNumberLength: Int       = 35
  val minTelephoneNumberLength: Int       = 6
  val authorisationNumberLength: Int      = 35
  val maxIncidentTextLength: Int          = 512
}
