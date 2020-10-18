package com.ahoubouby.applicative_traversable

import cats.instances.option._
import cats.instances.list._
import cats.instances.string._
import cats.data.Validated
import cats.Semigroupal

import cats.syntax.apply._ // for tupled and mapN

case class Cat(name: String, born: Int, color: String)

object SemigroupalExample {

  def main(args: Array[String]): Unit = {

    val res0: Option[(Int, String)] = Semigroupal[Option].product(Some(123), Some("abc"))
    val res1: Option[(Int, String)] = Semigroupal[Option].product(None, Some("abc"))
    val res2: Option[(Int, String)] = Semigroupal[Option].product(Some(123), None)
    Semigroupal.tuple3(Option(1), Option(2), Option(3))
    // res3: Option[(Int, Int, Int)] = Some((1,2,3))
    Semigroupal.tuple3(Option(1), Option(2), Option.empty[Int])
    // res4: Option[(Int, Int, Int)] = None
    Semigroupal.map3(Option(1), Option(2), Option(3))(_ + _ + _)
    // res5: Option[Int] = Some(6)
    Semigroupal.map2(Option(1), Option.empty[Int])(_ + _)
    // res6: Option[Int] = None
    println(res0)
    println(res1)
    println(res2)

    // ======================= APPLYSYNTAX ===================== //

    (Option(123), Option("abc")).tupled
    // res7: Option[(Int, String)] = Some((123,abc))

    val res9 = (
      Option("Garfield"),
      Option(1978),
      Option("Orange & black")
    ).mapN(Cat.apply)
    // res9: Option[Cat] = Some(Cat(Garfield,1978,Orange & black))
    val res10 = (Option("Garfield"), Option(1978), Option("Orange & black")).tupled
    println(res10)
    // ======================================================= //
  }
}
