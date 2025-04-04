/*
 * Copyright 2024 HM Revenue & Customs
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
import generators.Generators
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.DraftNoLongerAvailableView

class DraftNoLongerAvailableControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()

  "DraftNoLongerAvailableController" - {

    "must return Ok and the correct view for a GET" in {

      val request = FakeRequest(GET, controllers.routes.DraftNoLongerAvailableController.onPageLoad().url)

      val result = route(app, request).value

      val view = injector.instanceOf[DraftNoLongerAvailableView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString
    }

    "must redirect to 'view arrivals' for a POST" in {

      val request = FakeRequest(POST, controllers.routes.DraftNoLongerAvailableController.onSubmit().url)

      val result = route(app, request).value

      redirectLocation(result).value must include(frontendAppConfig.manageTransitMovementsViewArrivalsUrl)
    }
  }
}
