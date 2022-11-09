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

import models.DynamicAddress
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class InternationalDynamicAddressFormProvider @Inject() extends DynamicAddressFormProviderBase {
  override val postalCodeLength: Int = 17
}

object InternationalDynamicAddressFormProvider {

  def apply(prefix: String, isPostalCodeRequired: Boolean, args: Any*)(implicit messages: Messages): Form[DynamicAddress] =
    new InternationalDynamicAddressFormProvider()(prefix, isPostalCodeRequired, args: _*)
}