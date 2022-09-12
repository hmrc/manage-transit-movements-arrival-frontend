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

package pages.locationOfGoods

import models.UserAnswers
import models.locationOfGoods.QualifierOfIdentification
import org.scalacheck.Arbitrary.arbitrary
import pages.LocationOfGoods.QualifierOfIdentificationPage
import pages.QuestionPage
import pages.behaviours.PageBehaviours
import pages.sections.QualifierOfIdentificationDetailsSection
import play.api.libs.json.JsPath

class QualifierOfIdentificationPageSpec extends PageBehaviours {

  "QualifierofidentificationPage" - {

    beRetrievable[QualifierOfIdentification](QualifierOfIdentificationPage)

    beSettable[QualifierOfIdentification](QualifierOfIdentificationPage)

    beRemovable[QualifierOfIdentification](QualifierOfIdentificationPage)

    "cleanup" - {

      case object FakePage extends QuestionPage[String] {
        override def path: JsPath = QualifierOfIdentificationDetailsSection.path \ "fakeRoute"
      }

      "must remove previous answers when given a new answer" in {

        val sampleUa = arbitrary[UserAnswers].sample.value

        QualifierOfIdentification.values.foreach {
          qualifierOfIdentification =>
            val differentQualifierOfIdentification = QualifierOfIdentification.values.filterNot(_ == qualifierOfIdentification).head

            val result = sampleUa
              .set(QualifierOfIdentificationPage, qualifierOfIdentification)
              .success
              .value
              .set(FakePage, "fakeValue")
              .success
              .value
              .set(QualifierOfIdentificationPage, differentQualifierOfIdentification)
              .success
              .value

            result.get(FakePage) must not be defined
        }
      }

      "must not remove previous answers when given the same answer" in {

        val sampleUa = arbitrary[UserAnswers].sample.value

        QualifierOfIdentification.values.foreach {
          qualifierOfIdentification =>
            val result = sampleUa
              .set(QualifierOfIdentificationPage, qualifierOfIdentification)
              .success
              .value
              .set(FakePage, "fakeValue")
              .success
              .value
              .set(QualifierOfIdentificationPage, qualifierOfIdentification)
              .success
              .value

            result.get(FakePage) mustBe defined
        }
      }

    }

  }
}
