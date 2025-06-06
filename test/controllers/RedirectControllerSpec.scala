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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.SessionService

class RedirectControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val mockSessionService: SessionService = mock[SessionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[SessionService]).toInstance(mockSessionService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionService)
    when(mockSessionService.remove(any())(any())).thenCallRealMethod()
  }

  "return OK and the correct view for a GET" in {
    val request = FakeRequest(GET, routes.RedirectController.onPageLoad().url)

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual
      controllers.identification.routes.MovementReferenceNumberController.onPageLoad().url

    verify(mockSessionService).remove(any())(any())
  }
}
