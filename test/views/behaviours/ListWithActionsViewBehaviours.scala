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

package views.behaviours

import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import scala.collection.JavaConverters._

trait ListWithActionsViewBehaviours extends YesNoViewBehaviours {

  val allowMore: Boolean

  val listItems: Seq[ListItem]

  def pageWithListWithActions(): Unit =
    "page with a list with actions" - {
      "must contain a description list" in {
        val descriptionLists = getElementsByTag(doc, "dl")
        descriptionLists.size mustBe 1
      }

      val renderedItems = doc.getElementsByClass("hmrc-list-with-actions__item").asScala

      listItems.zipWithIndex.foreach {
        case (listItem, index) =>
          val renderedItem = renderedItems(index)

          s"item ${index + 1}" - {
            "must contain a name" in {
              val name = renderedItem.getElementsByClass("hmrc-list-with-actions__name").text()
              name mustBe listItem.name
            }

            "must contain 2 actions" in {
              val actions = renderedItem.getElementsByClass("hmrc-list-with-actions__action")
              actions.size() mustBe 2
            }

            "must contain a change link" in {
              val changeLink = renderedItem
                .getElementsByClass("hmrc-list-with-actions__action")
                .first()
                .getElementsByClass("govuk-link")
                .first()

              assertElementContainsHref(changeLink, listItem.changeUrl)

              changeLink.text() mustBe s"Change ${listItem.name}"
            }

            "must contain a remove link" in {
              val removeLink = renderedItem
                .getElementsByClass("hmrc-list-with-actions__action")
                .last()
                .getElementsByClass("govuk-link")
                .first()

              assertElementContainsHref(removeLink, listItem.removeUrl)

              removeLink.text() mustBe s"Remove ${listItem.name}"
            }
          }
      }
    }
}

trait MaxedOutListWithActionsViewBehaviours extends ListWithActionsViewBehaviours {
  override val allowMore: Boolean = false

  override val listItems: Seq[ListItem] = Seq(
    ListItem("1", "change-url-1", "remove-url-1"),
    ListItem("2", "change-url-2", "remove-url-2"),
    ListItem("3", "change-url-3", "remove-url-3")
  )
}

trait NonMaxedOutListWithActionsViewBehaviours extends ListWithActionsViewBehaviours {
  override val allowMore: Boolean = true

  override val listItems: Seq[ListItem] = Seq(
    ListItem("1", "change-url-1", "remove-url-1")
  )
}
