package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.$formProvider$
import models.requests.SpecificDataRequestProvider1
import models.{InternationalAddress, CountryList, MovementReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.$navRoute$
import $addressHolderNameImport$
import pages.$package$.$className$Page
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View
import navigation.{$navRoute$NavigatorProvider, UserAnswersNavigator}

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

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def form(countryList: CountryList)(implicit request: Request): Form[InternationalAddress] =
    formProvider("$package$.$className;format="decap"$", name, countryList)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage($addressHolderNamePage$))
    .async {
      implicit request =>
        countriesService.getCountries().map {
          countryList =>
            val preparedForm = request.userAnswers.get($className$Page) match {
              case None        => form(countryList)
              case Some(value) => form(countryList).fill(value)
            }

            Ok(view(preparedForm, mrn, mode, countryList.values, name))
          }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage($addressHolderNamePage$))
    .async {
      implicit request =>
        countriesService.getCountries().flatMap {
          countryList =>
            form(countryList)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, countryList.values, name))),
                value => {
                  val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  $className$Page.writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
                }
              )
            }
    }
}
