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

import base.SpecBase
import com.lucidchart.open.xtract.{ParseFailure, XmlReader}
import generators.MessagesModelGenerators
import models.XMLWrites._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.StreamlinedXmlEquality
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class InterchangeControlReferenceSpec extends SpecBase with ScalaCheckPropertyChecks with StreamlinedXmlEquality with MessagesModelGenerators {

  "InterchangeControlReference" - {
    "must convert to xml and convert to correct format" in {

      forAll(arbitrary[InterchangeControlReference]) {
        interchangeControlReference =>
          val expectedResult: NodeSeq =
            <IntConRefMES11>{s"AF${escapeXml(interchangeControlReference.date)}${interchangeControlReference.index}"}</IntConRefMES11>
          val result: NodeSeq = interchangeControlReference.toXml

          result mustEqual expectedResult
      }
    }

    "must deserialize from xml" in {

      forAll(arbitrary[InterchangeControlReference]) {
        interchangeControlReference =>
          val xml    = interchangeControlReference.toXml
          val result = XmlReader.of[InterchangeControlReference].read(xml).toOption.value

          result mustBe interchangeControlReference
      }
    }

    "must fail to deserialize from xml if invalid format" in {

      val invalidXml = <IntConRefMES11>Invalid format</IntConRefMES11>
      val result     = XmlReader.of[InterchangeControlReference].read(invalidXml)

      result mustBe an[ParseFailure]
    }
  }

}
