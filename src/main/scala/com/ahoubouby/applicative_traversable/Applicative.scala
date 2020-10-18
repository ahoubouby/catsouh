package com.ahoubouby.applicative_traversable

import cats.Semigroupal
import cats.Monad
import cats.instances.list._ // for Monoid
import cats.data.Validated
import cats.instances.string._
import cats.instances.either._
import cats.instances.int._
import cats.implicits._

trait Applicative[F[_]] extends Functor[F] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def pure[A](a: A): F[A]

  def map[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)
}

object Applicative {
  def main(args: Array[String]): Unit = {
    import cats.Semigroupal

    def parseInt(value: String): Either[String, Int] =
      Either.catchOnly[NumberFormatException](value.toInt).leftMap(_ => s"Couldn't read $value")

    val res0 = for {
      a <- parseInt("a")
      b <- parseInt("b")
      c <- parseInt("c")
    } yield (a + b + c) // Left(Couldn't read a)
    // its break at the first error not even use full

    println(res0)
    // correction
    def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] =
      for {
        a <- x
        b <- y
      } yield (a, b)

//    type AllErrorsOr[A] = Validated[List[String], A]

    //Semigroupal[AllErrorsOr]
    //.product(Validated.invalid(List("Error 1")), Validated.invalid(List("Error 2")))

  }
}
