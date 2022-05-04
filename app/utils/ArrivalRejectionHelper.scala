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

package utils

import models.ArrivalId
import models.messages.ErrorType.{DuplicateMrn, InvalidMrn, MRNError, UnknownMrn}
import models.messages.FunctionalError
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ArrivalRejectionHelper {

  implicit class RichFunctionalError(functionalError: FunctionalError) {

    def toSummaryList(implicit messages: Messages): SummaryList = SummaryList(
      rows = Seq(
        SummaryListRow(
          key = messages("arrivalRejection.errorCode").toKey,
          value = Value(functionalError.errorType.toString.toText)
        ),
        SummaryListRow(
          key = messages("arrivalRejection.pointer").toKey,
          value = Value(functionalError.pointer.value.toText)
        )
      )
    )
  }

  implicit class RichMRNError(mrnError: MRNError)(implicit messages: Messages) {

    def errorMessage: String = {
      val key = mrnError match {
        case UnknownMrn   => "unknown"
        case DuplicateMrn => "duplicate"
        case InvalidMrn   => "invalid"
      }
      messages(s"movementReferenceNumberRejection.error.$key")
    }

    def toSummaryList(arrivalId: ArrivalId, mrn: String): SummaryList = SummaryList(
      rows = Seq(
        SummaryListRow(
          key = messages("site.movement.reference.number").toKey,
          value = Value(mrn.toText),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  href = controllers.routes.UpdateRejectedMRNController.onPageLoad(arrivalId).url,
                  content = messages("site.edit").toText,
                  visuallyHiddenText = Some(messages("movementReferenceNumberRejection.change.hidden"))
                )
              )
            )
          )
        )
      )
    )
  }
}
