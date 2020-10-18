package com.ahoubouby.applicative_traversable

import cats.Monoid
import cats.instances.int._
import cats.instances.invariant._
import cats.instances.list._
import cats.instances.string._
import cats.syntax.apply._
import cats.syntax.semigroup._ // for |+|

case class CatF(
  name: String,
  yearOfBirth: Int,
  favoriteFoods: List[String]
)

object FancyFunctor {
  def main(args: Array[String]): Unit = {

    val tupleToCat: (String, Int, List[String]) => CatF = CatF.apply _
    val catToTuple: CatF => (String, Int, List[String]) = cat => (cat.name, cat.yearOfBirth, cat.favoriteFoods)

    implicit val catMonoid: Monoid[CatF] = (
      Monoid[String],
      Monoid[Int],
      Monoid[List[String]]
    ).imapN(tupleToCat)(catToTuple)

    val garfield   = CatF("Garfield", 1978, List("Lasagne"))
    val heathcliff = CatF("Heathcliff", 1988, List("Junk Food"))
    val res0       = garfield |+| heathcliff
    println(res0)
  }
}
