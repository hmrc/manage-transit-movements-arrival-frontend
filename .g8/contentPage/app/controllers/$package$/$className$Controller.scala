package controllers.$package$

import controllers.actions._
import javax.inject.Inject
import models.MovementReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

class $className;format="cap"$Controller @Inject()(
    override val messagesApi: MessagesApi,
    actions: Actions,
    val controllerComponents: MessagesControllerComponents,
    view: $className$View
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      Ok(view(mrn))
  }
}
