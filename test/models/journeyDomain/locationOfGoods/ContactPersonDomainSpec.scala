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

package models.journeyDomain.locationOfGoods

import base.SpecBase
import generators.Generators
import org.scalacheck.Gen
import pages.locationOfGoods.{ContactPersonNamePage, ContactPersonTelephonePage}

class ContactPersonDomainSpec extends SpecBase with Generators {

  private val name = Gen.alphaNumStr.sample.value
  private val tel  = Gen.alphaNumStr.sample.value

  "ContactPersonDomain" - {

    "can be parsed from user answers" - {
      "when all questions answered" in {
        val userAnswers = emptyUserAnswers
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        val expectedResult = ContactPersonDomain(
          name = name,
          phoneNumber = tel
        )

        val result = ContactPersonDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ContactPersonNamePage,
          ContactPersonTelephonePage
        )
      }
    }

    "cannot be parsed from user answers" - {
      "when name is unanswered" in {
        val result = ContactPersonDomain.userAnswersReader.apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustEqual ContactPersonNamePage
        result.left.value.pages mustEqual Seq(
          ContactPersonNamePage
        )
      }

      "when telephone number is unanswered" in {
        val userAnswers = emptyUserAnswers.setValue(ContactPersonNamePage, name)

        val result = ContactPersonDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual ContactPersonTelephonePage
        result.left.value.pages mustEqual Seq(
          ContactPersonNamePage,
          ContactPersonTelephonePage
        )
      }
    }
  }
}
