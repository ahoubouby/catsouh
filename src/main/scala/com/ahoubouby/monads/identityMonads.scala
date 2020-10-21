package com.ahoubouby.monads

import cats.{ Id, Monad }
import cats.syntax.flatMap._
import cats.syntax.applicative._
import cats.syntax.functor._

object identityMonads extends App {
  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
    for {
      aa <- a
      bb <- b
    } yield aa * bb

  //compilation error
  // sumSquare(3, 4)
  //correction to that's is usinng id

  val res0 = sumSquare(3: Id[Int], 4: Id[Int])
  assert(res0 == 12.pure[Id], "should return square of 4*3 (monad Id) ")
}
