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

package handlers

import javax.inject.{Inject, Singleton}
import logging.Logging
import models.requests.DataRequest
import play.api.PlayException
import play.api.http.HttpErrorHandler
import play.api.http.Status._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, RequestHeader, Result}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.http.ApplicationException
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

// NOTE: There should be changes to bootstrap to make this easier, the API in bootstrap should allow a `Future[Html]` rather than just an `Html`
@Singleton
class ErrorHandler @Inject() (renderer: Renderer, val messagesApi: MessagesApi)(implicit ec: ExecutionContext)
    extends HttpErrorHandler
    with I18nSupport
    with NunjucksSupport
    with Logging {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String = ""): Future[Result] =
    statusCode match {
      case NOT_FOUND =>
        Future.successful(Redirect(controllers.routes.ErrorController.notFound()))
      case result if isClientError(result) =>
        Future.successful(Redirect(controllers.routes.ErrorController.badRequest()))
      case _ =>
        Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
    }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {

    logError(request, exception)

    exception match {
      case ApplicationException(result, _) =>
        Future.successful(result)
      case _ =>
        Future.successful(Redirect(controllers.routes.ErrorController.internalServerError()))
    }
  }

  def onConcurrentError(message: String, redirectLink: String, journey: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    val json = Json.obj(
      "pageTitle"    -> msg"concurrent.remove.error.title".withArgs(msg"$journey"),
      "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs(msg"$journey"),
      "linkText"     -> msg"concurrent.remove.error.$message.link.text",
      "redirectLink" -> redirectLink
    )
    renderer.render("concurrentRemoveError.njk", json).map(NotFound(_))
  }

  private def logError(request: RequestHeader, ex: Throwable): Unit =
    logger.error(
      """
        |
        |! %sInternal server error, for (%s) [%s] ->
        | """.stripMargin.format(
        ex match {
          case p: PlayException => "@" + p.id + " - "
          case _                => ""
        },
        request.method,
        request.uri
      ),
      ex
    )
}
