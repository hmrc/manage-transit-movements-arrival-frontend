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

package viewModels.components

import play.twirl.api.Html

sealed trait InputRadioViewModel

object InputRadioViewModel {

  case class Radio(
    heading: String,
    caption: Option[String] = None
  ) extends InputRadioViewModel

  case class RadioWithAdditionalHtml(
    heading: String,
    caption: Option[String] = None,
    additionalHtml: Html
  ) extends InputRadioViewModel
      with AdditionalHtmlViewModel

  case class RadioWithLegend(
    legend: String
  ) extends InputRadioViewModel
}