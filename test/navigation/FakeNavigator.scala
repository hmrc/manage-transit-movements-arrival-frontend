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

package navigation

import config.FrontendAppConfig
import models.{Index, Mode, UserAnswers}
import play.api.mvc.Call

class FakeNavigator(desiredRoute: Call) extends Navigator {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeAuthorisationNavigator(desiredRoute: Call, index: Index, mode: Mode)(implicit config: FrontendAppConfig) extends AuthorisationNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeAuthorisationsNavigator(desiredRoute: Call, mode: Mode)(implicit config: FrontendAppConfig) extends AuthorisationsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeIncidentNavigator(desiredRoute: Call, index: Index, mode: Mode)(implicit config: FrontendAppConfig) extends IncidentNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeIdentificationNavigator(desiredRoute: Call, mode: Mode)(implicit config: FrontendAppConfig) extends IdentificationNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeLocationOfGoodsNavigator(desiredRoute: Call, mode: Mode)(implicit config: FrontendAppConfig) extends LocationOfGoodsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeArrivalNavigator(desiredRoute: Call, mode: Mode)(implicit config: FrontendAppConfig) extends ArrivalNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}
