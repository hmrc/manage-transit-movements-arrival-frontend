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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import forms.{FormConstants, PostTransitionConstants, TransitionConstants}
import models.journeyDomain.JourneyDomainModel
import models.transitionJourneyDomain.ArrivalDomainTransition
import navigation._
import navigation.annotations.{Constants, IdentificationDetails, LocationOfGoods}
import services.{DateTimeService, DateTimeServiceImpl}

import java.time.Clock

class TransitionModule extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[Navigator]).annotatedWith(classOf[IdentificationDetails]).to(classOf[IdentificationNavigator])
    bind(classOf[IncidentNavigatorProvider]).to(classOf[IncidentNavigatorProviderImpl])
    bind(classOf[AuthorisationNavigatorProvider]).to(classOf[AuthorisationNavigatorProviderImpl])

    // New transition modules:
    bind(classOf[Navigator]).annotatedWith(classOf[LocationOfGoods]).to(classOf[LocationOfGoodsTransitionNavigator])
    bind(classOf[FormConstants]).annotatedWith(classOf[Constants]).to(classOf[TransitionConstants])

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction]).asEagerSingleton()
    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[SpecificDataRequiredActionProvider]).to(classOf[SpecificDataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[RemoveInProgressActionProvider]).to(classOf[RemoveInProgressActionProviderImpl])

    bind(classOf[DateTimeService]).to(classOf[DateTimeServiceImpl]).asEagerSingleton()
    bind(classOf[Clock]).toInstance(Clock.systemUTC)
  }
}
