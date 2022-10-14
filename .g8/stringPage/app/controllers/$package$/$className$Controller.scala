package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.$formProvider$
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.$navRoute$
import pages.$package$.$className$Page
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
    navigatorProvider: $navRoute$NavigatorProvider,
    formProvider: $formProvider$,
    actions: Actions,
    val controllerComponents: MessagesControllerComponents,
    view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("$package$.$className;format="decap"$")

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mrn, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
       .bindFromRequest()
       .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          $className$Page.writeToUserAnswers(value).writeToSession().navigate()
      }
    )
  }
}
