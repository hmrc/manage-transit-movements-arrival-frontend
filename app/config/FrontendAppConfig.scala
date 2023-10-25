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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

import java.time.LocalDate

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val contactHost: String = configuration.get[String]("contact-frontend.host")

  val showPhaseBanner: Boolean        = configuration.get[Boolean]("banners.showPhase")
  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")
  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")

  val signOutUrl: String          = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  val loginHmrcServiceUrl: String = configuration.get[String]("urls.loginHmrcService")

  lazy val manageTransitMovementsUrl: String = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val serviceUrl: String                = s"$manageTransitMovementsUrl/what-do-you-want-to-do"

  lazy val manageTransitMovementsViewArrivalsUrl: String = s"$manageTransitMovementsUrl/view-arrival-notifications"

  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val customsReferenceDataUrl: String = configuration.get[Service]("microservice.services.customs-reference-data").fullServiceUrl

  lazy val timeoutSeconds: Int   = configuration.get[Int]("session.timeoutSeconds")
  lazy val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  lazy val enrolmentProxyUrl: String = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl

  lazy val nctsHelpdeskUrl: String  = configuration.get[String]("urls.nctsHelpdesk")
  lazy val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")

  lazy val legacyEnrolmentKey: String           = configuration.get[String]("keys.legacy.enrolmentKey")
  lazy val legacyEnrolmentIdentifierKey: String = configuration.get[String]("keys.legacy.enrolmentIdentifierKey")

  lazy val newEnrolmentKey: String           = configuration.get[String]("keys.enrolmentKey")
  lazy val newEnrolmentIdentifierKey: String = configuration.get[String]("keys.enrolmentIdentifierKey")

  lazy val eccEnrolmentSplashPage: String = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val maxIdentificationAuthorisations: Int = configuration.get[Int]("limits.maxIdentificationAuthorisations")
  lazy val maxIncidents: Int                    = configuration.get[Int]("limits.maxIncidents")
  lazy val maxSeals: Int                        = configuration.get[Int]("limits.maxSeals")
  lazy val maxNumberOfItems: Int                = configuration.get[Int]("limits.maxNumberOfItems")
  lazy val maxTransportEquipments: Int          = configuration.get[Int]("limits.maxTransportEquipments")

  lazy val endorsementDateMin: LocalDate = LocalDate.of(
    configuration.get[Int]("dates.endorsementDateMin.year"),
    configuration.get[Int]("dates.endorsementDateMin.month"),
    configuration.get[Int]("dates.endorsementDateMin.day")
  )

  lazy val cacheUrl: String = configuration.get[Service]("microservice.services.manage-transit-movements-arrival-cache").fullServiceUrl
}
