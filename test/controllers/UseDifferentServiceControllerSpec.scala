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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.mockito.ArgumentCaptor
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UseDifferentServiceView

class UseDifferentServiceControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  "UseDifferentService Controller" - {

    "return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)
      val request = FakeRequest(GET, routes.UseDifferentServiceController.onPageLoad(mrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[UseDifferentServiceView]
      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString
    }
  }
}
