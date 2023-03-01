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

import generated.{Flag, Number0, Number1}
import play.api.libs.json._
import scalaxb.XMLCalendar

import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar
import scala.language.implicitConversions

package object api {

  lazy val identificationPath: JsPath = __ \ "identification"
  lazy val authorisationsPath: JsPath = __ \ "identification" \ "authorisations"

  implicit class RichJsPath(path: JsPath) {

    def readArray[T](implicit reads: Int => Reads[T]): Reads[Seq[T]] =
      path.readWithDefault[Seq[T]](Nil) {
        case value: JsArray => JsSuccess(value.readValuesAs[T])
        case _              => throw new Exception(s"$path did not contain an array")
      }
  }

  implicit class RichJsArray(arr: JsArray) {

    def zipWithIndex: List[(JsValue, Int)] = arr.value.toList.zipWithIndex

    def readValuesAs[T](implicit reads: Int => Reads[T]): Seq[T] =
      arr.mapWithSequenceNumber {
        case (value, index) => value.as[T](reads(index))
      }

    def mapWithSequenceNumber[T](f: (JsValue, Int) => T): Seq[T] =
      arr.zipWithIndex.map {
        case (value, i) => f(value, i + 1)
      }
  }

  implicit class RichOptionalJsArray(arr: Option[JsArray]) {

    def readValuesAs[T](implicit reads: Int => Reads[T]): Seq[T] =
      arr.mapWithSequenceNumber {
        case (value, index) => value.as[T](reads(index))
      }

    def mapWithSequenceNumber[T](f: (JsValue, Int) => T): Seq[T] =
      arr.map(_.mapWithSequenceNumber(f)).getOrElse(Nil)
  }

  implicit class RichOptionalJsObject(obj: Option[JsObject]) {

    def readValueAs[T](implicit reads: Reads[T]): Option[T] =
      obj.map(_.as[T])
  }

  implicit def boolToFlag(x: Option[Boolean]): Option[Flag] =
    x.map(boolToFlag)

  implicit def boolToFlag(x: Boolean): Flag =
    if (x) Number1 else Number0

  implicit def localDateToXMLGregorianCalendar(date: Option[LocalDate]): Option[XMLGregorianCalendar] =
    date.map(localDateToXMLGregorianCalendar)

  implicit def localDateToXMLGregorianCalendar(date: LocalDate): XMLGregorianCalendar =
    stringToXMLGregorianCalendar(date.toString)

  implicit def stringToXMLGregorianCalendar(date: Option[String]): Option[XMLGregorianCalendar] =
    date.map(stringToXMLGregorianCalendar)

  implicit def stringToXMLGregorianCalendar(date: String): XMLGregorianCalendar =
    XMLCalendar(date.replace("Z", ""))

  implicit def successfulReads[T](value: T): Reads[T] = Reads {
    _ => JsSuccess(value)
  }

}
