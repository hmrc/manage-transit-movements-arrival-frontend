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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalActionProvider, IdentifierAction}
import derivable.DeriveNumberOfEvents
import handlers.ErrorHandler
import models.GoodsLocation.{AuthorisedConsigneesLocation, BorderForceOffice}
import models.{CheckMode, Index, MovementReferenceNumber, UserAnswers}
import pages.GoodsLocationPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ArrivalSubmissionService
import uk.gov.hmrc.govukfrontend.views.Aliases.{SummaryList, SummaryListRow}
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{AddEventsHelper, CheckYourAnswersHelper}
import viewModels.sections.ViewModelConfig
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject() (override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalActionProvider,
                                            requireData: DataRequiredAction,
                                            service: ArrivalSubmissionService,
                                            errorHandler: ErrorHandler,
                                            val viewModelConfig: ViewModelConfig,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with HttpErrorFunctions {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData) {
    implicit request =>
      val answers = createSections(request.userAnswers)

      Ok(view(mrn, answers))
  }

  def onPost(mrn: MovementReferenceNumber): Action[AnyContent] =
    (identify andThen getData(mrn) andThen requireData).async {
      implicit request =>
        service.submit(request.userAnswers) flatMap {
          case Some(result) =>
            result.status match {
              case status if is2xx(status) => Future.successful(Redirect(routes.ConfirmationController.onPageLoad(mrn)))
              case status if is4xx(status) => errorHandler.onClientError(request, status)
              case _                       => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
            }
          case None => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
        }
    }

  def createSections(userAnswers: UserAnswers)(implicit messages: Messages): Seq[(Option[String], SummaryList)] = {
    val helper = new CheckYourAnswersHelper(userAnswers, CheckMode)
    val mrn    = (None, new SummaryList(Seq(helper.movementReferenceNumber)))

    val whereAreTheGoods = (
      Some(messages("checkYourAnswers.section.goodsLocation")),
      userAnswers.get(GoodsLocationPage) match {
        case Some(AuthorisedConsigneesLocation) =>
          new SummaryList(Seq(helper.goodsLocation, helper.authorisedLocation).flatten)
        case _ =>
          new SummaryList(Seq(helper.goodsLocation, helper.authorisedLocation, helper.customsSubPlace, helper.customsOffice).flatten)
      }
    )

    val traderDetails = (
      Some(messages("checkYourAnswers.section.traderDetails")),
      new SummaryList(
        Seq(helper.traderName, helper.traderEori, helper.traderAddress).flatten
      )
    )

    val consigneeDetails = (
      Some(messages("checkYourAnswers.section.consigneeDetails")),
      new SummaryList(
        Seq(helper.consigneeName, helper.eoriNumber, helper.consigneeAddress, helper.pickCustomsOffice).flatten
      )
    )

    val placeOfNotification = (
      Some(messages("checkYourAnswers.section.placeOfNotificationDetails")),
      new SummaryList(
        Seq(helper.isTraderAddressPlaceOfNotification, helper.placeOfNotification).flatten
      )
    )

    val events = (
      Some(messages("checkYourAnswers.section.events")),
      new SummaryList(helper.incidentOnRoute.toSeq ++ eventList(userAnswers))
    )

    userAnswers.get(GoodsLocationPage) match {
      case Some(BorderForceOffice) => Seq(mrn, whereAreTheGoods, traderDetails, placeOfNotification, events)
      case _                       => Seq(mrn, whereAreTheGoods, consigneeDetails, events)
    }
  }

  private def eventList(userAnswers: UserAnswers)(implicit messages: Messages): List[SummaryListRow] = {
    val numberOfEvents = userAnswers.get(DeriveNumberOfEvents).getOrElse(0)
    val cyaHelper      = new AddEventsHelper(userAnswers, CheckMode)
    val listOfEvents   = List.range(0, numberOfEvents).map(Index(_))
    listOfEvents.flatMap(cyaHelper.cyaListOfEvent)
  }
}
