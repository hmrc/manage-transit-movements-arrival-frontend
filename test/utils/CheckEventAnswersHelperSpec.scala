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

import base.SpecBase
import controllers.events.routes._
import controllers.events.seals.routes._
import controllers.events.transhipments.routes._
import models.domain.{ContainerDomain, SealDomain}
import models.reference.{Country, CountryCode}
import models.{CheckMode, CountryList, Mode, TranshipmentType}
import pages.events._
import pages.events.seals._
import pages.events.transhipments._
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

class CheckEventAnswersHelperSpec extends SpecBase {

  val mode: Mode = CheckMode

  "CheckEventAnswersHelper" - {

    ".isTranshipment" - {

      "must return None" - {
        "when IsTranshipmentPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.isTranshipment(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when IsTranshipmentPage defined" in {

          val answers = emptyUserAnswers
            .set(IsTranshipmentPage(eventIndex), true)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.isTranshipment(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("isTranshipment.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(messages("site.yes").toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = IsTranshipmentController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("isTranshipment.change.hidden")),
                      attributes = Map("id" -> s"change-is-transhipment-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".transhipmentType" - {

      val transhipmentType = TranshipmentType.DifferentContainer

      "must return None" - {
        "when TranshipmentTypePage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.transhipmentType(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when TranshipmentTypePage defined" in {

          val answers = emptyUserAnswers
            .set(TranshipmentTypePage(eventIndex), transhipmentType)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.transhipmentType(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("transhipmentType.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(messages(s"transhipmentType.checkYourAnswers.$transhipmentType").toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = TranshipmentTypeController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("transhipmentType.change.hidden")),
                      attributes = Map("id" -> s"transhipment-type-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".containerNumber" - {

      val containerDomain = ContainerDomain("NUMBER")

      "must return None" - {
        "when ContainerNumberPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.containerNumber(eventIndex, containerIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when ContainerNumberPage defined" in {

          val answers = emptyUserAnswers
            .set(ContainerNumberPage(eventIndex, containerIndex), containerDomain)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.containerNumber(eventIndex, containerIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("addContainer.containerList.label", containerIndex.display).toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(containerDomain.containerNumber.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = ContainerNumberController.onPageLoad(mrn, eventIndex, containerIndex, mode).url,
                      visuallyHiddenText = Some(messages("containerNumber.change.hidden", containerDomain.containerNumber)),
                      attributes = Map("id" -> s"change-container-${containerIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".eventCountry" - {

      val countryCode = CountryCode("CODE")

      "must return None" - {
        "when EventCountryPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.eventCountry(eventIndex)(CountryList(Nil)) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when EventCountryPage defined" - {

          "country not found" in {

            val answers = emptyUserAnswers
              .set(EventCountryPage(eventIndex), countryCode)
              .success
              .value

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.eventCountry(eventIndex)(CountryList(Nil)) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = messages("eventCountry.checkYourAnswersLabel").toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(countryCode.code.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = messages("site.edit").toText,
                        href = EventCountryController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some(messages("eventCountry.change.hidden")),
                        attributes = Map("id" -> s"change-event-country-${eventIndex.display}")
                      )
                    )
                  )
                )
              )
            )
          }

          "country found" in {

            val country = Country(countryCode, "DESCRIPTION")

            val answers = emptyUserAnswers
              .set(EventCountryPage(eventIndex), countryCode)
              .success
              .value

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.eventCountry(eventIndex)(CountryList(Seq(country))) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = messages("eventCountry.checkYourAnswersLabel").toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(country.description.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = messages("site.edit").toText,
                        href = EventCountryController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some(messages("eventCountry.change.hidden")),
                        attributes = Map("id" -> s"change-event-country-${eventIndex.display}")
                      )
                    )
                  )
                )
              )
            )
          }
        }
      }
    }

    ".eventPlace" - {

      val place = "PLACE"

      "must return None" - {
        "when EventPlacePage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.eventPlace(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when EventPlacePage defined" in {

          val answers = emptyUserAnswers
            .set(EventPlacePage(eventIndex), place)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.eventPlace(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("eventPlace.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(place.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = EventPlaceController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("eventPlace.change.hidden")),
                      attributes = Map("id" -> s"change-event-place-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".eventReported" - {

      "must return None" - {
        "when EventReportedPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.eventReported(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when EventReportedPage defined" in {

          val answers = emptyUserAnswers
            .set(EventReportedPage(eventIndex), false)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.eventReported(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("eventReported.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(messages("site.no").toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = EventReportedController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("eventReported.change.hidden")),
                      attributes = Map("id" -> s"change-event-reported-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".incidentInformation" - {

      val incident = "INCIDENT"

      "must return None" - {
        "when IncidentInformationPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.incidentInformation(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when IncidentInformationPage defined" in {

          val answers = emptyUserAnswers
            .set(IncidentInformationPage(eventIndex), incident)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.incidentInformation(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("incidentInformation.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(incident.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = IncidentInformationController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("incidentInformation.change.hidden")),
                      attributes = Map("id" -> s"change-incident-information-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".transportIdentity" - {

      val vehicle = "VEHICLE"

      "must return None" - {
        "when TransportIdentityPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.transportIdentity(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when TransportIdentityPage defined" in {

          val answers = emptyUserAnswers
            .set(TransportIdentityPage(eventIndex), vehicle)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.transportIdentity(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("transportIdentity.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(vehicle.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = TransportIdentityController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("transportIdentity.change.hidden")),
                      attributes = Map("id" -> s"transport-identity-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".transportNationality" - {

      val countryCode = CountryCode("CODE")

      "must return None" - {
        "when TransportNationalityPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.transportNationality(eventIndex)(CountryList(Nil)) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when TransportNationalityPage defined" - {

          "country not found" in {

            val answers = emptyUserAnswers
              .set(TransportNationalityPage(eventIndex), countryCode)
              .success
              .value

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.transportNationality(eventIndex)(CountryList(Nil)) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = messages("transportNationality.checkYourAnswersLabel").toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(countryCode.code.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = messages("site.edit").toText,
                        href = TransportNationalityController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some(messages("transportNationality.change.hidden")),
                        attributes = Map("id" -> s"transport-nationality-${eventIndex.display}")
                      )
                    )
                  )
                )
              )
            )
          }

          "country found" in {

            val country = Country(countryCode, "DESCRIPTION")

            val answers = emptyUserAnswers
              .set(TransportNationalityPage(eventIndex), countryCode)
              .success
              .value

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.transportNationality(eventIndex)(CountryList(Seq(country))) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = messages("transportNationality.checkYourAnswersLabel").toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(country.description.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = messages("site.edit").toText,
                        href = TransportNationalityController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some(messages("transportNationality.change.hidden")),
                        attributes = Map("id" -> s"transport-nationality-${eventIndex.display}")
                      )
                    )
                  )
                )
              )
            )
          }
        }
      }
    }

    ".haveSealsChanged" - {

      "must return None" - {
        "when HaveSealsChangedPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.haveSealsChanged(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when HaveSealsChangedPage defined" in {

          val answers = emptyUserAnswers
            .set(HaveSealsChangedPage(eventIndex), true)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.haveSealsChanged(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("haveSealsChanged.checkYourAnswersLabel").toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(messages("site.yes").toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = HaveSealsChangedController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some(messages("haveSealsChanged.change.hidden")),
                      attributes = Map("id" -> s"seals-changed-${eventIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

    ".sealIdentity" - {

      val seal = SealDomain("NUMBER")

      "must return None" - {
        "when SealIdentityPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.sealIdentity(eventIndex, sealIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when SealIdentityPage defined" in {

          val answers = emptyUserAnswers
            .set(SealIdentityPage(eventIndex, sealIndex), seal)
            .success
            .value

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.sealIdentity(eventIndex, sealIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("addSeal.sealList.label", sealIndex.display).toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(seal.numberOrMark.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = SealIdentityController.onPageLoad(mrn, eventIndex, sealIndex, mode).url,
                      visuallyHiddenText = Some(messages("sealIdentity.change.hidden", seal.numberOrMark)),
                      attributes = Map("id" -> s"change-seal-${sealIndex.display}")
                    )
                  )
                )
              )
            )
          )
        }
      }
    }

  }

}
