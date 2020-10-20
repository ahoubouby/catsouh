package com.ahoubouby.monads
import cats.{ Monad, _ }
import cats.instances.list._
import cats.instances.option._
import cats.instances.vector._ // for Monad

trait Monad[F[_]] {
  def pure[A](value: A): F[A]
  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
  def map[A, B](value: F[A])(func: A => B): F[B] =
    flatMap(value)(func.andThen(pure)) // or flatMap(value)(a => pure(func(a)
}
object monads extends App {

  import scala.util.Try

  implicit def optionMonad(implicit app: Applicative[Option]) =
    new Monad[Option] {
      // Define flatMap using Option's flatten method
      override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
        app.map(fa)(f).flatten

      // Reuse this definition from Applicative.
      override def pure[A](a: A): Option[A] = app.pure(a)

      @annotation.tailrec
      def tailRecM[A, B](init: A)(fn: A => Option[Either[A, B]]): Option[B] =
        fn(init) match {
          case None           => None
          case Some(Right(b)) => Some(b)
          case Some(Left(a))  => tailRecM(a)(fn)
        }
    }

  def parseInt(value: String) = Try(value.toInt).toOption
  def divide(a: Int, b: Int): Option[Int] =
    if (b == 0) None else Some(a / b)

  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
    parseInt(aStr).flatMap(v1 => parseInt(bStr).flatMap(v2 => divide(v1, v2)))

  val res0 = stringDivideBy("6", "2")
  val res1 = stringDivideBy("6", "0")

  def proofMonadsAreFunctors(aStr: String, bStr: String) =
    for {
      v1 <- parseInt(aStr)
      v2 <- parseInt(bStr)
      ans <- divide(v1, v2)
    } yield ans

  val res3 = proofMonadsAreFunctors("6", "2")
  val res4 = proofMonadsAreFunctors("6", "0")
  assert(res0 == res3, "they should be equal we just improve syntax")
  assert(res1 == res4, "they should be equal we just improve syntax")

  // ----------------------- monads in cats ----------------------- //

  val opt1 = Monad[Option].pure(3) // opt1: Option[Int] = Some(3)
  val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2)) // opt2: Option[Int] = Some(5)
  val opt3 = Monad[Option].map(opt2)(a => 100 * a) // opt3: Option[Int] = Some(500)

  val list1 = Monad[List].pure(3) // list1: List[Int] = List(3)
  val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10)) // list2: List[Int] = List(1, 10, 2, 20, 3, 30)
  val list3 = Monad[List].map(list2)(a => a + 123) // list3: List[Int] = List(124, 133, 125, 143, 126, 153)

  Monad[Option].flatMap(Option(1))(a => Option(a * 2)) // res0: Option[Int] = Some(2)
  Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10)) // res1: List[Int] = List(1, 10, 2, 20, 3, 30)

  Monad[Vector].flatMap(Vector(1, 2, 3))(a => Vector(a, a * 10)) // res2: Vector[Int] = Vector(1, 10, 2, 20, 3, 30)
  // ----------------------- monads in cats ----------------------- //
}
