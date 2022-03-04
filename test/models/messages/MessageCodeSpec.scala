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

package models.messages

import models.XMLWrites._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.StreamlinedXmlEquality
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class MessageCodeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with StreamlinedXmlEquality {

  "MessageCode" - {
    "must create valid xml" in {

      forAll(arbitrary[String]) {
        code =>
          val messageCode: MessageCode = MessageCode(code)
          val expectedResult: NodeSeq  = <MesTypMES20>{code}</MesTypMES20>

          messageCode.toXml mustEqual expectedResult
      }
    }
  }

}
