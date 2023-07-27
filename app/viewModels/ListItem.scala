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

package viewModels

abstract class ParentListItem(val name: String, val changeUrl: String, val removeUrl: Option[String]) {
  def args: Seq[String]
}

case class ListItem(
  override val name: String,
  override val changeUrl: String,
  override val removeUrl: Option[String]
) extends ParentListItem(name, changeUrl, removeUrl) {
  def args: Seq[String] = Seq(name)
}

case class ListItemWithSuffixHiddenArg(
  override val name: String,
  override val changeUrl: String,
  override val removeUrl: Option[String],
  hiddenSuffixArg: String
) extends ParentListItem(name, changeUrl, removeUrl) {
  override def args: Seq[String] = Seq(hiddenSuffixArg, name)
}
