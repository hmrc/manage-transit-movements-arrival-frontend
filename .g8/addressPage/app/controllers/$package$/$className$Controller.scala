package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.$formProvider$
import models.reference.Country
import models.requests.SpecificDataRequestProvider2
import models.{DynamicAddress, MovementReferenceNumber, Mode}
import navigation.{$navRoute$NavigatorProvider, UserAnswersNavigator}
import pages.$package$.$className$Page
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  navigatorProvider: $navRoute$NavigatorProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: $formProvider$,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: $className$View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider2[String, Country]#SpecificDataRequest[?]

  private def name(implicit request: Request): String = request.arg._1

  private def country(implicit request: Request): Country = request.arg._2

  private def form(isPostalCodeRequired: Boolean)(implicit request: Request): Form[DynamicAddress] =
    formProvider("$package$.$className;format="decap"$", isPostalCodeRequired, name)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage.getFirst(NamePage))
    .andThen(getMandatoryPage.getSecond(CountryPage))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).map {
          isPostalCodeRequired =>
            val preparedForm = request.userAnswers.get($className$Page) match {
              case None        => form(isPostalCodeRequired)
              case Some(value) => form(isPostalCodeRequired).fill(value)
            }

            Ok(view(preparedForm, mrn, mode, name, isPostalCodeRequired))
          }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage.getFirst(NamePage))
    .andThen(getMandatoryPage.getSecond(CountryPage))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).flatMap {
          isPostalCodeRequired =>
            form(isPostalCodeRequired)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, name, isPostalCodeRequired))),
                value => {
                  val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  $className$Page.writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
                }
              )
            }
    }
}
