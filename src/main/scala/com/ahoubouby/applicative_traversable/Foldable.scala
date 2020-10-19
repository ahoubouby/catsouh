package com.ahoubouby.applicative_traversable

import cats.Foldable
import cats.instances.int._
import cats.instances.vector._
import cats.instances.list._

import scala.math.Numeric
object FoldableExample {
  def show[A](list: List[A]): String = list.foldLeft("nil")((accum, item) => s"$item then $accum")

}
object foldable {
  import FoldableExample._
  import cats.kernel.Monoid
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3)
    val res0 = show[Int](list)
    println(res0)
//Scaf-fold-ing Other Methods
    def map[A, B](list: List[A])(f: A => B): List[B] =
      list.foldRight(List.empty[B])((a, acc) => f(a) :: acc)

    def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = list.foldRight(List.empty[B]) { (a, acc) =>
      f(a) ::: acc

    }

    def filter[A](list: List[A])(f: A => Boolean): List[A] =
      list.foldRight(List.empty[A])((a, acc) => if (f(a)) a :: acc else acc)
  }

  def sumWithNumeric[A](list: List[A])(implicit numeric: Numeric[A]): A = list.foldRight(numeric.zero)(numeric.plus)
  def sumWithMonoid[A](list: List[A])(implicit monoid: Monoid[A]): A    = list.foldRight(monoid.empty)(monoid.combine)
  assert(sumWithNumeric(List(1, 2, 3)) == sumWithMonoid(List(1, 2, 3)), "they must be equal")

  import cats.Foldable
  import cats.instances.int._
  import cats.instances.vector._
  import cats.instances.list._

  val ints = List(Vector(1, 2, 3), Vector(4, 5, 6))
  Foldable[List].compose(Foldable[Vector]).combineAll(ints)
}
