package controllers.incident.location

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.UnLocodeFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.IncidentNavigator
import pages.incident.location.UnLocodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.UnLocodeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.location.UnLocodeView
import navigation.{IncidentNavigatorNavigatorProvider, UserAnswersNavigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnLocodeController @Inject()(
   override val messagesApi: MessagesApi,
   implicit val sessionRepository: SessionRepository,
   navigatorProvider: IncidentNavigatorNavigatorProvider,
   actions: Actions,
   formProvider: UnLocodeFormProvider,
   service: UnLocodeService,
   val controllerComponents: MessagesControllerComponents,
   view: UnLocodeView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getUnLocodes.map {
        unLocodeList =>
          val form = formProvider("incident.location.unLocode", unLocodeList)
          val preparedForm = request.userAnswers.get(UnLocodePage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, unLocodeList.unLocodes, mode))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getUnLocodes.flatMap {
        unLocodeList =>
          val form = formProvider("incident.location.unLocode", unLocodeList)
          form.bindFromRequest().fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, unLocodeList.unLocodes, mode))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
              UnLocodePage.writeToUserAnswers(value).writeToSession().navigate()
            }
        )
      }
  }
}
