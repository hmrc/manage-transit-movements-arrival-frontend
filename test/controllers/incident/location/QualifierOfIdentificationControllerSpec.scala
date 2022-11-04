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

package controllers.incident.location

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.locationOfGoods.QualifierOfIdentificationFormProvider
import models.NormalMode
import models.locationOfGoods.QualifierOfIdentification
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.location.QualifierOfIdentificationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.html.incident.location.QualifierOfIdentificationView

import scala.concurrent.Future

class QualifierOfIdentificationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                        = new QualifierOfIdentificationFormProvider()
  private val form                                = formProvider()
  private val mode                                = NormalMode
  private lazy val qualifierOfIdentificationRoute = routes.QualifierOfIdentificationController.onPageLoad(mrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))

  "QualifierOfIdentification Controller" - {

    "must return OK and the correct view for a GET" in {

      val radioItems: Seq[RadioItem] = Seq(
        RadioItem(content = "Coordinates".toText, id = Some("value"), value = Some("coordinates"), checked = false),
        RadioItem(content = "UN/LOCODE".toText, id = Some("value_1"), value = Some("unlocode"), checked = false),
        RadioItem(content = "Address".toText, id = Some("value_2"), value = Some("address"), checked = false)
      )

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, (_, _) => radioItems, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(QualifierOfIdentificationPage(index), QualifierOfIdentification.Coordinates)
      setExistingUserAnswers(userAnswers)

      val radioItems: Seq[RadioItem] = Seq(
        RadioItem(content = "Coordinates".toText, id = Some("value"), value = Some("coordinates"), checked = true),
        RadioItem(content = "UN/LOCODE".toText, id = Some("value_1"), value = Some("unlocode"), checked = false),
        RadioItem(content = "Address".toText, id = Some("value_2"), value = Some("address"), checked = false)
      )

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> QualifierOfIdentification.values.head.toString))

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, (_, _) => radioItems, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, qualifierOfIdentificationRoute)
        .withFormUrlEncodedBody(("value", QualifierOfIdentification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val radioItems: Seq[RadioItem] = Seq(
        RadioItem(content = "Coordinates".toText, id = Some("value"), value = Some("coordinates"), checked = false),
        RadioItem(content = "UN/LOCODE".toText, id = Some("value_1"), value = Some("unlocode"), checked = false),
        RadioItem(content = "Address".toText, id = Some("value_2"), value = Some("address"), checked = false)
      )

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, qualifierOfIdentificationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, (_, _) => radioItems, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, qualifierOfIdentificationRoute)
        .withFormUrlEncodedBody(("value", QualifierOfIdentification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}