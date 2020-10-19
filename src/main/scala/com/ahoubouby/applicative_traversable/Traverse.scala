package com.ahoubouby.applicative_traversable

import cats.Applicative

//object tree {
//
//  sealed abstract class Tree[A] extends Product with Serializable {
//    def traverse[F[_]: Applicative, B](f: A => F[B]): F[Tree[B]] = this match {
//      case Tree.Empty()         => Applicative[F].pure(Tree.Empty())
//      case Tree.Branch(v, l, r) => Applicative[F].map3(f(v), l.traverse(f), r.traverse(f))(Tree.Branch(_, _, _))
//    }
//  }
//  final case class Empty[A]()                                         extends Tree[A]
//  final case class Branch[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]
//
//}

trait Traverse[F[_]] {
  def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
}

object Traverse {
  def traverse[F[_]: Applicative, A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(Applicative[F].pure(List.empty[B])) { (a: A, acc: F[List[B]]) =>
      val fb: F[B] = f(a)
      Applicative[F].map2(fb, acc)(_ :: _)
    }

  implicit val traverseList = new Traverse[List] {
    override def traverse[G[_]: Applicative, A, B](fa: List[A])(f: A => G[B]): G[List[B]] =
      fa.foldRight(Applicative[G].pure(List.empty[B]))((a, acc) => Applicative[G].map2(f(a), acc)(_ :: _))
  }
}
