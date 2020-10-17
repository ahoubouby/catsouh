package com.ahoubouby.applicative_traversable

trait Functor[F[_]] {
  def map[A, B](as: F[A])(f: A => B): F[B]
  def left[A, B](f: A => B): F[A] => F[B] = fa => map(fa)(f)
}
object Functor {

  implicit val functorForOption: Functor[Option] = new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None    => None
      case Some(a) => Some(f(a))
    }
  }
}
