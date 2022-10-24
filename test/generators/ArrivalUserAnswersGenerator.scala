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

package generators

import base.SpecBase
import models.UserAnswers
import models.journeyDomain.ArrivalDomain
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import org.scalacheck.Gen

trait ArrivalUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators with SpecBase =>

  def arbitraryIdentificationAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[IdentificationDomain](userAnswers)

  def arbitraryLocationOfGoodsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[LocationOfGoodsDomain](userAnswers)

  def arbitraryArrivalAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ArrivalDomain](userAnswers)(ArrivalDomain.userAnswersReader)

}