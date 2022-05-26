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
import uk.gov.hmrc.govukfrontend.views.Aliases._
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
            .setValue(IsTranshipmentPage(eventIndex), true)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.isTranshipment(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "Were the goods moved to a different vehicle or container?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value("Yes".toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = IsTranshipmentController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("if the goods were moved to a different vehicle or container"),
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
            .setValue(TranshipmentTypePage(eventIndex), transhipmentType)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.transhipmentType(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "Where were the goods moved?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value("A different container".toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = TranshipmentTypeController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("where the goods moved to"),
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

    ".container" - {

      val containerDomain = ContainerDomain("NUMBER")

      "must return None" - {
        "when ContainerNumberPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.container(eventIndex, containerIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when ContainerNumberPage defined" in {

          val answers = emptyUserAnswers
            .setValue(ContainerNumberPage(eventIndex, containerIndex), containerDomain)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.container(eventIndex, containerIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = s"Container ${containerIndex.display}".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(containerDomain.containerNumber.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = ContainerNumberController.onPageLoad(mrn, eventIndex, containerIndex, mode).url,
                      visuallyHiddenText = Some(s"container ${containerIndex.display}"),
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
              .setValue(EventCountryPage(eventIndex), countryCode)

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.eventCountry(eventIndex)(CountryList(Nil)) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = "Which country were the goods in when the event happened?".toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(countryCode.code.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = EventCountryController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some("which country the goods were in when the event happened"),
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
              .setValue(EventCountryPage(eventIndex), countryCode)

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.eventCountry(eventIndex)(CountryList(Seq(country))) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = "Which country were the goods in when the event happened?".toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(country.description.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = EventCountryController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some("which country the goods were in when the event happened"),
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
            .setValue(EventPlacePage(eventIndex), place)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.eventPlace(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "Where did the event happen?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(place.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = EventPlaceController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("where the event happened"),
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
            .setValue(EventReportedPage(eventIndex), false)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.eventReported(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "Has this event been reported to customs?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value("No".toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = EventReportedController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("if this event has been reported to customs"),
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
            .setValue(IncidentInformationPage(eventIndex), incident)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.incidentInformation(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "What happened on the journey?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(incident.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = IncidentInformationController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("what happened on the journey"),
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
            .setValue(TransportIdentityPage(eventIndex), vehicle)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.transportIdentity(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "What is the name, registration or reference of the new vehicle?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(vehicle.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = TransportIdentityController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("the name, registration or reference of the new vehicle"),
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
              .setValue(TransportNationalityPage(eventIndex), countryCode)

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.transportNationality(eventIndex)(CountryList(Nil)) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = "Which country is the vehicle registered in?".toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(countryCode.code.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = TransportNationalityController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some("the country where the vehicle is registered"),
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
              .setValue(TransportNationalityPage(eventIndex), countryCode)

            val helper = new CheckEventAnswersHelper(answers, mode)
            helper.transportNationality(eventIndex)(CountryList(Seq(country))) mustBe Some(
              SummaryListRow(
                key = Key(
                  content = "Which country is the vehicle registered in?".toText,
                  classes = "govuk-!-width-one-half"
                ),
                value = Value(country.description.toText),
                actions = Some(
                  new Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = TransportNationalityController.onPageLoad(mrn, eventIndex, mode).url,
                        visuallyHiddenText = Some("the country where the vehicle is registered"),
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
            .setValue(HaveSealsChangedPage(eventIndex), true)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.haveSealsChanged(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = "Have any of the official customs seals changed?".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value("Yes".toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = HaveSealsChangedController.onPageLoad(mrn, eventIndex, mode).url,
                      visuallyHiddenText = Some("if any of the official customs seals have changed"),
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

    ".seal" - {

      val seal = SealDomain("NUMBER")

      "must return None" - {
        "when SealIdentityPage undefined" in {

          val helper = new CheckEventAnswersHelper(emptyUserAnswers, mode)
          helper.seal(eventIndex, sealIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when SealIdentityPage defined" in {

          val answers = emptyUserAnswers
            .setValue(SealIdentityPage(eventIndex, sealIndex), seal)

          val helper = new CheckEventAnswersHelper(answers, mode)
          helper.seal(eventIndex, sealIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = s"Official customs seal ${sealIndex.display}".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(seal.numberOrMark.toText),
              actions = Some(
                new Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = SealIdentityController.onPageLoad(mrn, eventIndex, sealIndex, mode).url,
                      visuallyHiddenText = Some(s"official customs seal ${sealIndex.display}"),
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
