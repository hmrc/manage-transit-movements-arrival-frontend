package controllers.incident.equipment.itemNumber

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.Identification
import pages.incident.equipment.itemNumber.ConfirmRemoveItemNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView
import navigation.{IdentificationNavigatorProvider, UserAnswersNavigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveItemNumberController @Inject()(
    override val messagesApi: MessagesApi,
    implicit val sessionRepository: SessionRepository,
    navigatorProvider: IdentificationNavigatorProvider,
    actions: Actions,
    formProvider: YesNoFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: ConfirmRemoveItemNumberView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("incident.equipment.itemNumber.confirmRemoveItemNumber")

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ConfirmRemoveItemNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          ConfirmRemoveItemNumberPage.writeToUserAnswers(value).writeToSession().navigate()
        }
      )
  }
}
