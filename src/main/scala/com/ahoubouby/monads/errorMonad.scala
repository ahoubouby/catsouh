package com.ahoubouby.monads

trait ErrorMonad[F[_], E] extends Monad[F] {
  // Lift an error into the `F` context:
  def raiseError[A](e: E): F[A]
  // Handle an error, potentially recovering from it:
  def handleError[A](fa: F[A])(f: E => A): F[A]
  // Test an instance of `F`,
  // failing if the predicate is not satisfied:
  def ensure[A](fa: F[A])(e: E)(f: A => Boolean): F[A]
}

object errorMonad extends App {
  import cats.MonadError
  import cats.instances.either._

  type ErrorOr[A] = Either[String, A]
  val monadError = MonadError[ErrorOr, String]
  val success    = monadError.pure(42)
  val failure    = monadError.raiseError("Badness")
  println(success)
  println(failure)
  monadError.handleErrorWith(failure)({
    case "Badness" => monadError.pure("It's ok")
    case other     => monadError.raiseError("It's not ok")
  })

  monadError.ensure(success)("Number too low!")(_ > 1000)

}
