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

import forms.mappings.Mappings
import javax.inject.Inject
import models.UkAddressLine._
import models.UkAddress
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

class UkAddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, name: String)(implicit messages: Messages): Form[UkAddress] =
    Form(
      mapping(
        BuildingAndStreet.field -> {
          lazy val args = Seq(BuildingAndStreet.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(BuildingAndStreet.length, s"$prefix.error.length", Seq(BuildingAndStreet.arg.capitalize, name, BuildingAndStreet.length)),
                regexp(BuildingAndStreet.regex, s"$prefix.error.invalid", Seq(BuildingAndStreet.arg.capitalize, name))
              )
            )
        },
        City.field -> {
          lazy val args = Seq(City.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(City.length, s"$prefix.error.length", Seq(City.arg.capitalize, name, City.length)),
                regexp(City.regex, s"$prefix.error.invalid", Seq(City.arg.capitalize, name))
              )
            )
        },
        PostCode.field -> {
          lazy val args = Seq(name)
          trimmedText(s"$prefix.error.postalCode.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostCode.length, s"$prefix.error.postalCode.length", args :+ PostCode.length),
                regexp(PostCode.regex, s"$prefix.error.postalCode.invalid", args)
              )
            )
        }
      )(UkAddress.apply)(UkAddress.unapply)
    )
}
