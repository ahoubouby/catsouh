package com.ahoubouby.applicative_traversable

import cats.Functor
import cats.Applicative
import cats.data.Nested
import cats.implicits._
import cats.syntax.semigroupal._
object entryPoint {
  def main(args: Array[String]): Unit = {
    def needsFunctor[F[_]: Functor, A](fa: F[A]): F[Unit] = Functor[F].map(fa)(_ => ())
    type ListOption[A] = List[Option[A]]
    val listOption = List(Some(1), None, Some(2))
    val res0       = listOption.map(_.map(_ + 1))
    val res1       = Functor[List].compose[Option].map(listOption)(_ + 1)
    assert(res0 == res1, "compose functor not working tho")

    val nested: Nested[List, Option, Int] = Nested(listOption)
    nested.map(_ + 1)
    //def foo: List[Option[Unit]] = {
    //val listOptionFunctor = Functor[List].compose[Option]
    //needsFunctor[ListOption, Int](listOption)(listOptionFunctor)
    //} // compilation error
    //The Nested approach, being a distinct type from its constituents,
    //will resolve the usual way modulo possible SI-2712 issues (which can be addressed through partial unification),
    //but requires syntactic and runtime overhead from wrapping and unwrapping.

    //***********************************************************************

    def product3[F[_]: Applicative, A, B, C](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] = {
      val F    = Applicative[F]
      val fabc = F.product(F.product(fa, fb), fc)
      F.map(fabc) { case ((a, b), c) => (a, b, c) }
    }

    val f: (Int, Char) => Double = (i, c) => (i + c).toDouble

    val int: Option[Int]   = Some(5)
    val char: Option[Char] = Some('a')
    val res2               = int.map(i => (c: Char) => f(i, c))
    // res0: Option[Char => Double] = Some($$Lambda$12522/1812926345@558c4da3)

  }
}
