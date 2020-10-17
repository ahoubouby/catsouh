package com.ahoubouby.applicative_traversable

import cats.Functor
import cats.data.Nested
import cats.implicits._

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
  }
}
