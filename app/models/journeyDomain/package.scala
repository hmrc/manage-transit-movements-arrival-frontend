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

package models

import cats.data.ReaderT
import models.journeyDomain.OpsError.ReaderError
import pages.sections.Section
import pages.{InferredPage, Page, ReadOnlyPage}
import play.api.libs.json.Reads
import queries.Gettable

package object journeyDomain {

  type EitherType[A]        = Either[ReaderError, A]
  type UserAnswersReader[A] = ReaderT[EitherType, UserAnswers, ReaderSuccess[A]]

  object UserAnswersReader {
    def apply[A](implicit ev: UserAnswersReader[A]): UserAnswersReader[A] = ev

    def apply[A](fn: UserAnswers => EitherType[ReaderSuccess[A]]): UserAnswersReader[A] =
      ReaderT[EitherType, UserAnswers, ReaderSuccess[A]](fn)

    def success[A](a: A): Read[A] = success {
      (_: UserAnswers) => a
    }

    def success[A](f: UserAnswers => A): Read[A] = pages => {
      val fn: UserAnswers => EitherType[ReaderSuccess[A]] = ua => Right(ReaderSuccess(f(ua), pages))
      apply(fn)
    }

    def none[A]: Read[Option[A]] = pages => success[Option[A]](None).apply(pages)
  }

  implicit class GettableAsFilterForNextReaderOps[A: Reads](a: Gettable[A]) {

    /** Returns UserAnswersReader[Option[B]], where UserAnswersReader[B] which is run only if UserAnswerReader[A] is defined and satisfies the predicate, if it
      * defined and does not satisfy the predicate overall reader will will return None. If the result of UserAnswerReader[A] is not defined then the overall
      * reader will fail and `next` will not be run
      */
    def filterOptionalDependent[B](predicate: A => Boolean)(next: => Read[B]): Read[Option[B]] = pages =>
      a.reader(s"Reader for ${a.path} failed before reaching predicate")
        .apply(pages)
        .flatMap {
          case ReaderSuccess(x, pages) =>
            if (predicate(x)) {
              next.toOption.apply(pages)
            } else {
              UserAnswersReader.none.apply(pages)
            }
        }
  }

  implicit class GettableAsReaderOps[A](a: Gettable[A]) {

    /** Returns a reader for [[Gettable]], which will succeed with an [[A]] if the value is defined and will fail if it is not defined
      */

    def reader(implicit reads: Reads[A]): Read[A] = reader(None)

    def readerNoAppend(implicit reads: Reads[A]): Read[A] = reader(None, append = false)

    def reader(message: String)(implicit reads: Reads[A]): Read[A] = reader(Some(message))

    private def reader(message: Option[String], append: Boolean = true)(implicit reads: Reads[A]): Read[A] = pages => {
      lazy val updatedPages = if (append) pages.append(a) else pages
      val fn: UserAnswers => EitherType[ReaderSuccess[A]] = _.get(a) match {
        case Some(value) => Right(ReaderSuccess(value, updatedPages))
        case None        => Left(ReaderError(a, updatedPages, message))
      }
      UserAnswersReader(fn)
    }
  }

  type Pages   = Seq[Page]
  type Read[T] = Pages => UserAnswersReader[T]

  object Read {
    def apply[T](value: T): Read[T] = UserAnswersReader.success(value).apply(_)
  }

  implicit class RichPages(pages: Pages) {

    def append(page: Page): Pages =
      page match {
        case _: Section[?]             => pages
        case _: InferredPage[?]        => pages
        case _: ReadOnlyPage[?]        => pages
        case _ if pages.contains(page) => pages
        case _                         => pages :+ page
      }

    def append(page: Option[Section[?]]): Pages =
      page.fold(pages) {
        case x if pages.contains(x) => pages
        case x                      => pages :+ x
      }
  }

  implicit class RichRead[A](value: Read[A]) {

    def map[T <: JourneyDomainModel](fun: A => T): Read[T] =
      to {
        a =>
          val t = fun(a)
          pages =>
            UserAnswersReader.apply {
              ua => Right(ReaderSuccess(t, pages.append(t.page(ua))))
            }
      }

    def to[T](fun: A => Read[T]): Read[T] = pages =>
      for {
        a      <- value(pages)
        reader <- fun(a.value)(a.pages)
      } yield reader

    def toOption: Read[Option[A]] = value(_).map(_.toOption)
    def toSeq: Read[Seq[A]]       = value(_).map(_.toSeq)
  }

  implicit class RichTuple2[A, B](value: (Read[A], Read[B])) {

    def map[T <: JourneyDomainModel](fun: (A, B) => T): Read[T] =
      to {
        case (a, b) =>
          val t = fun(a, b)
          pages =>
            UserAnswersReader.apply {
              ua => Right(ReaderSuccess(t, pages.append(t.page(ua))))
            }
      }

    def to[T](fun: (A, B) => Read[T]): Read[T] = pages =>
      for {
        a      <- value._1(pages)
        b      <- value._2(a.pages)
        reader <- fun(a.value, b.value)(b.pages)
      } yield reader
  }

  implicit class RichTuple3[A, B, C](value: (Read[A], Read[B], Read[C])) {

    def map[T <: JourneyDomainModel](fun: (A, B, C) => T): Read[T] =
      to {
        case (a, b, c) =>
          val t = fun(a, b, c)
          pages =>
            UserAnswersReader.apply {
              ua => Right(ReaderSuccess(t, pages.append(t.page(ua))))
            }
      }

    def to[T](fun: (A, B, C) => Read[T]): Read[T] = pages =>
      for {
        a      <- value._1(pages)
        b      <- value._2(a.pages)
        c      <- value._3(b.pages)
        reader <- fun(a.value, b.value, c.value)(c.pages)
      } yield reader
  }

  implicit class RichTuple4[A, B, C, D](value: (Read[A], Read[B], Read[C], Read[D])) {

    def map[T <: JourneyDomainModel](fun: (A, B, C, D) => T): Read[T] =
      to {
        case (a, b, c, d) =>
          val t = fun(a, b, c, d)
          pages =>
            UserAnswersReader.apply {
              ua => Right(ReaderSuccess(t, pages.append(t.page(ua))))
            }
      }

    def to[T](fun: (A, B, C, D) => Read[T]): Read[T] = pages =>
      for {
        a      <- value._1(pages)
        b      <- value._2(a.pages)
        c      <- value._3(b.pages)
        d      <- value._4(c.pages)
        reader <- fun(a.value, b.value, c.value, d.value)(d.pages)
      } yield reader
  }

}
