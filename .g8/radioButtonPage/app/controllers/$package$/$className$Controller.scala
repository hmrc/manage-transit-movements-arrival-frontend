package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.$package$.$formProvider$
import models.{Mode, MovementReferenceNumber}
import models.$package$.$className$
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

class $className$Controller @Inject()(
   override val messagesApi: MessagesApi,
   implicit val sessionRepository: SessionRepository,
   @$navRoute$ implicit val navigator: Navigator,
   actions: Actions,
   formProvider: $formProvider$,
   val controllerComponents: MessagesControllerComponents,
   view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, $className$.radioItems, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, $className$.radioItems, mode))),
        value => $className$Page.writeToUserAnswers(value).writeToSession().navigateWith(mode)
      )
  }
}
