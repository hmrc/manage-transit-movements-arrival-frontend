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

package pages.incident

import controllers.incident.routes
import models.{Index, Mode, RichOptionJsArray, UserAnswers}
import pages.QuestionPage
import pages.incident.equipment.{ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import pages.sections.incident.{EquipmentsSection, IncidentSection}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.{Success, Try}

case class ContainerIndicatorYesNoPage(index: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = IncidentSection(index).path \ toString

  override def toString: String = "containerIndicatorYesNo"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.ContainerIndicatorYesNoController.onPageLoad(userAnswers.mrn, mode, index))

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    lazy val numberOfEquipments = userAnswers.get(EquipmentsSection(index)).length

    def removeEquipmentPage[T](page: (Index, Index) => QuestionPage[T], userAnswers: UserAnswers): Try[UserAnswers] =
      (0 until numberOfEquipments).foldLeft[Try[UserAnswers]](Success(userAnswers)) {
        (acc, i) =>
          acc match {
            case Success(value) => value.remove(page(index, Index(i)))
            case _              => acc
          }
      }

    value match {
      case Some(false) =>
        removeEquipmentPage(ContainerIdentificationNumberPage, userAnswers)
      case Some(true) =>
        userAnswers
          .remove(AddTransportEquipmentPage(index))
          .flatMap(removeEquipmentPage(ContainerIdentificationNumberYesNoPage, _))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
