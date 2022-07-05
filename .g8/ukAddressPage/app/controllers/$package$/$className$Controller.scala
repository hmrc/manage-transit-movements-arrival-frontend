package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.$formProvider$
import models.requests.SpecificDataRequestProvider1
import models.{UkAddress, MovementReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.$navRoute$
import $addressHolderNameImport$
import pages.$package$.$className$Page
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @$navRoute$ implicit val navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: $formProvider$,
  val controllerComponents: MessagesControllerComponents,
  view: $className$View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def form()(implicit request: Request): Form[UkAddress] =
    formProvider("$package$.$className;format="decap"$", name)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage($addressHolderNamePage$)) {
      implicit request =>
        val preparedForm = request.userAnswers.get($className$Page) match {
          case None        => form()
          case Some(value) => form().fill(value)
        }

        Ok(view(preparedForm, mrn, mode, name))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage($addressHolderNamePage$))
    .async {
      implicit request =>
        form()
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, name))),
            value => $className$Page.writeToUserAnswers(value).writeToSession().navigateWith(mode)
          )
    }
}
