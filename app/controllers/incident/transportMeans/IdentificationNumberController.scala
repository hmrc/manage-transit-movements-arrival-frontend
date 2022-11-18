package controllers.incident.transportMeans

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.IdentificationNumberFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.Incident
import pages.incident.transportMeans.IdentificationNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.transportMeans.IdentificationNumberView
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentificationNumberController @Inject()(
    override val messagesApi: MessagesApi,
    implicit val sessionRepository: SessionRepository,
    navigatorProvider: IncidentNavigatorProvider,
    formProvider: IdentificationNumberFormProvider,
    actions: Actions,
    val controllerComponents: MessagesControllerComponents,
    view: IdentificationNumberView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("incident.transportMeans.identificationNumber")

  private type Request = SpecificDataRequestProvider1[InlandMode]#SpecificDataRequest[_]

  private def identificationType(implicit request: Request): Option[Identification] = request.arg match {
    case InlandMode.Unknown => Some(Identification.Unknown)
    case _ => request.userAnswers.get(IdentificationPage)
  }

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(???)) {
        implicit request =>
          identificationType match {
            case Some(value) =>
              val form = formProvider("incident.transportMeans.identificationNumber", value.arg)
              val preparedForm = request.userAnswers.get(IdentificationNumberPage) match {
                case None => form
                case Some(value) => form.fill(value)
              }
              Ok(view(preparedForm, mrn, mode, incidentIndex, value))
            case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
          }
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
       .bindFromRequest()
       .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          IdentificationNumberPage.writeToUserAnswers(value).writeToSession().navigate()
      }
    )
  }
}
