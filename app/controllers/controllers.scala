/*
 * Copyright 2023 HM Revenue & Customs
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

import cats.data.ReaderT
import models.UserAnswers
import models.journeyDomain.OpsError.WriterError
import models.requests.MandatoryDataRequest
import navigation.Navigator
import pages.QuestionPage
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.Results.Redirect
import play.api.mvc.{Call, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object controllers {

  type EitherType[A]        = Either[WriterError, A]
  type UserAnswersWriter[A] = ReaderT[EitherType, UserAnswers, A]
  type Write[A]             = (QuestionPage[A], UserAnswers)

  implicit class SettableOps[A](page: QuestionPage[A]) {

    def writeToUserAnswers(value: A)(implicit writes: Writes[A], reads: Reads[A]): UserAnswersWriter[Write[A]] =
      ReaderT[EitherType, UserAnswers, Write[A]](
        userAnswers =>
          userAnswers.set[A](page, value) match {
            case Success(value)     => Right((page, value))
            case Failure(exception) => Left(WriterError(page, Some(s"Failed to write $value to page $page with exception: ${exception.toString}")))
          }
      )
  }

  implicit class SettableOpsRunner[A](userAnswersWriter: UserAnswersWriter[Write[A]]) {

    def writeToSession(
      userAnswers: UserAnswers,
      sessionRepository: SessionRepository
    )(implicit executionContext: ExecutionContext, hc: HeaderCarrier): Future[Write[A]] =
      userAnswersWriter.run(userAnswers) match {
        case Left(opsError) => Future.failed(new Exception(s"${opsError.toString}"))
        case Right(value) =>
          sessionRepository
            .set(value._2)
            .map(
              _ => value
            )
      }

    def writeToSession(sessionRepository: SessionRepository)(implicit
      dataRequest: MandatoryDataRequest[?],
      ex: ExecutionContext,
      hc: HeaderCarrier
    ): Future[Write[A]] = writeToSession(dataRequest.userAnswers, sessionRepository)
  }

  implicit class NavigatorOps[A](write: Future[Write[A]]) {

    def navigateWith(navigator: Navigator)(implicit executionContext: ExecutionContext): Future[Result] =
      navigate {
        case (page, userAnswers) => navigator.nextPage(userAnswers, Some(page))
      }

    private def navigate(result: Write[A] => Call)(implicit executionContext: ExecutionContext): Future[Result] =
      write.map {
        w => Redirect(result(w))
      }
  }
}
