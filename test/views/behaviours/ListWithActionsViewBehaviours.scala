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

package views.behaviours

import generators.Generators
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import play.twirl.api.TwirlHelperImports._
import viewModels.{ListItem, ListItemWithSuffixHiddenArg, ParentListItem}

trait ListWithActionsViewBehaviours extends YesNoViewBehaviours with Generators {

  def maxNumber: Int

  val additionalHiddenArgs: Boolean = false

  private val listItem: ParentListItem                    = arbitrary[ListItem].sample.value
  private val listItemWithSuffixHiddenArg: ParentListItem = arbitrary[ListItemWithSuffixHiddenArg].sample.value

  val listItems: Seq[ParentListItem]                    = Seq(listItem)
  val listItemsWithSuffixHiddenArg: Seq[ParentListItem] = Seq(listItemWithSuffixHiddenArg)

  val maxedOutListItems: Seq[ParentListItem]                    = Seq.fill(maxNumber)(listItem)
  val maxedOutListItemsWithSuffixHiddenArg: Seq[ParentListItem] = Seq.fill(maxNumber)(listItemWithSuffixHiddenArg)

  def applyMaxedOutView: HtmlFormat.Appendable

  def pageWithMoreItemsAllowed(h1Args: Any*)(h2Args: Any*): Unit =
    "page with more items allowed" - {

      behave like pageWithTitle(doc, s"$prefix.singular", h1Args: _*)

      behave like pageWithHeading(doc, s"$prefix.singular", h1Args: _*)

      if (additionalHiddenArgs) {
        behave like pageWithListWithActions(doc, listItemsWithSuffixHiddenArg)
      } else {
        behave like pageWithListWithActions(doc, listItems)
      }

      behave like pageWithRadioItems(legendIsHeading = false, args = h2Args)
    }

  def pageWithItemsMaxedOut(args: Any*): Unit =
    "page with items maxed out" - {

      val doc = parseView(applyMaxedOutView)

      behave like pageWithTitle(doc, s"$prefix.plural", args: _*)

      behave like pageWithHeading(doc, s"$prefix.plural", args: _*)

      if (additionalHiddenArgs) {
        behave like pageWithListWithActions(doc, maxedOutListItemsWithSuffixHiddenArg)
      } else {
        behave like pageWithListWithActions(doc, maxedOutListItems)
      }

      behave like pageWithoutRadioItems(doc)

      behave like pageWithContent(doc, "p", messages(s"$prefix.maxLimit.label"))
    }

  // scalastyle:off method.length
  private def pageWithListWithActions(doc: Document, listItems: Seq[ParentListItem]): Unit =
    "page with a list with actions" - {
      "must contain a description list" in {
        val descriptionLists = getElementsByTag(doc, "dl")
        descriptionLists.size mustBe 1
      }

      val renderedItems = doc.getElementsByClass("govuk-summary-list__row").toList

      listItems.zipWithIndex.foreach {
        case (listItem, index) =>
          val renderedItem = renderedItems(index)

          s"item ${index + 1}" - {
            "must contain a name" in {
              val name = renderedItem.getElementsByClass("govuk-summary-list__key").text()
              name mustBe listItem.name
            }

            listItem.removeUrl match {
              case Some(removeUrl) =>
                val actions = renderedItem.getElementsByClass("govuk-summary-list__actions-list-item")
                "must contain 2 actions" in {
                  actions.size() mustBe 2
                }
                chooseActionLink(additionalHiddenArgs, actions, "Change", 0, listItem.changeUrl)
                chooseActionLink(additionalHiddenArgs, actions, "Remove", 1, removeUrl)
              case None =>
                val actions = renderedItem.getElementsByClass("govuk-summary-list__actions")
                "must contain 1 action" in {
                  actions.size() mustBe 1
                }
                chooseActionLink(additionalHiddenArgs, actions, "Change", 0, listItem.changeUrl)
            }

            def chooseActionLink(
              additionalHiddenArgs: Boolean,
              actions: Elements,
              linkType: String,
              index: Int,
              url: String
            ): Unit = if (additionalHiddenArgs) {
              withActionLinkSuffixHiddenArg(actions, linkType, index, url)
            } else {
              withActionLink(actions, linkType, index, url)
            }

            def withActionLink(actions: Elements, linkType: String, index: Int, url: String): Unit =
              s"must contain a $linkType link" in {
                val link = actions
                  .toList(index)
                  .getElementsByClass("govuk-link")
                  .first()

                assertElementContainsHref(link, url)

                val spans = link.getElementsByTag("span")
                spans.size() mustBe 2

                spans.first().text() mustBe linkType
                assert(spans.first().hasAttr("aria-hidden"))

                spans.last().text() mustBe s"$linkType ${listItem.name}"
                assert(spans.last().hasClass("govuk-visually-hidden"))
              }

            def withActionLinkSuffixHiddenArg(actions: Elements, linkType: String, index: Int, url: String): Unit =
              s"must contain a $linkType link" in {

                val link = actions
                  .toList(index)
                  .getElementsByClass("govuk-link")
                  .first()

                assertElementContainsHref(link, url)

                val spans = link.getElementsByTag("span")
                spans.size() mustBe 2

                spans.first().text() mustBe linkType
                assert(spans.first().hasAttr("aria-hidden"))

                spans.last().text() mustBe s"$linkType ${listItem.args.head} ${listItem.name}"
                assert(spans.last().hasClass("govuk-visually-hidden"))
              }
          }
      }
    }
  // scalastyle:on method.length
}
