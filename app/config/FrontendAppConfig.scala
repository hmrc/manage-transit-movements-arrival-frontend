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
import controllers.routes
import models.MovementReferenceNumber
import play.api.Configuration
import play.api.i18n.Messages
import play.api.mvc.Request

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  lazy val phase6Enabled: Boolean = configuration.get[Boolean]("feature-flags.phase-6-enabled")

  val apiVersion: String = if (phase6Enabled) "2.0" else "1.0"

  lazy val countriesOfDestination: Seq[String] = configuration.get[Seq[String]]("countriesOfDestination")

  val contactHost: String = configuration.get[String]("contact-frontend.host")

  val showPhaseBanner: Boolean        = configuration.get[Boolean]("banners.showPhase")
  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")
  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")

  val signOutUrl: String          = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  val loginHmrcServiceUrl: String = configuration.get[String]("urls.loginHmrcService")

  lazy val manageTransitMovementsUrl: String = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val feedbackUrl: String               = configuration.get[String]("urls.feedback")
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

  lazy val enrolmentKey: String           = configuration.get[String]("enrolment.key")
  lazy val enrolmentIdentifierKey: String = configuration.get[String]("enrolment.identifierKey")

  lazy val eccEnrolmentSplashPage: String = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val maxIdentificationAuthorisations: Int = configuration.get[Int]("limits.maxIdentificationAuthorisations")

  lazy val cacheUrl: String = configuration.get[Service]("microservice.services.manage-transit-movements-arrival-cache").fullServiceUrl

  val isTraderTest: Boolean            = configuration.get[Boolean]("trader-test.enabled")
  val feedbackEmail: String            = configuration.get[String]("trader-test.feedback.email")
  val feedbackForm: String             = configuration.get[String]("trader-test.feedback.link")
  val allowedRedirectUrls: Seq[String] = configuration.get[Seq[String]]("urls.allowedRedirects")

  def signOutAndUnlockUrl(mrn: Option[MovementReferenceNumber]): String = mrn.map(routes.DeleteLockController.delete(_, None).url).getOrElse(signOutUrl)

  def mailto(implicit request: Request[?], messages: Messages): String = {
    val subject = messages("site.email.subject")
    val body = {
      val newLine      = "%0D%0A"
      val newParagraph = s"$newLine$newLine"
      s"""
         |URL: ${request.uri}$newParagraph
         |Tell us how we can help you here.$newParagraph
         |Give us a brief description of the issue or question, including details like…$newLine
         | - The screens where you experienced the issue$newLine
         | - What you were trying to do at the time$newLine
         | - The information you entered$newParagraph
         |Please include your name and phone number and we’ll get in touch.
         |""".stripMargin
    }

    s"mailto:$feedbackEmail?subject=$subject&body=$body"
  }
}
