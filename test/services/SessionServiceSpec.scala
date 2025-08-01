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

package services

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest

class SessionServiceSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val sessionService = new SessionService()

  "get" - {
    "when MRN exists" - {
      "must return Some value" in {
        forAll(arbitrary[String]) {
          value =>
            implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> value)
            sessionService.get.get mustEqual value
        }
      }
    }

    "when MRN does not exist" - {
      "must return None" in {
        implicit val request: FakeRequest[?] = fakeRequest
        sessionService.get mustNot be(defined)
      }
    }
  }

  "set" - {
    "must set MRN in session" in {
      implicit val request: FakeRequest[?] = fakeRequest
      val resultBefore                     = Ok
      resultBefore.session.get(SessionService.key) mustNot be(defined)
      val resultAfter = sessionService.set(resultBefore, mrn)
      resultAfter.session.get(SessionService.key).get mustEqual mrn.toString
    }

    "must overwrite MRN in session" in {
      implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> "foo")
      val resultBefore                     = Ok
      resultBefore.session.get(SessionService.key) must be(defined)
      val resultAfter = sessionService.set(resultBefore, mrn)
      resultAfter.session.get(SessionService.key).get mustEqual mrn.toString
    }
  }

  "remove" - {
    "must remove MRN from session" - {
      "when there isn't an MRN in the session" in {
        implicit val request: FakeRequest[?] = fakeRequest
        val resultBefore                     = Ok
        resultBefore.session.get(SessionService.key) mustNot be(defined)
        val resultAfter = sessionService.remove(resultBefore)
        resultAfter.session.get(SessionService.key) mustNot be(defined)
      }

      "when there is an MRN in the session" in {
        implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> mrn.toString)
        val resultBefore                     = Ok
        resultBefore.session.get(SessionService.key) must be(defined)
        val resultAfter = sessionService.remove(resultBefore)
        resultAfter.session.get(SessionService.key) mustNot be(defined)
      }
    }
  }
}
