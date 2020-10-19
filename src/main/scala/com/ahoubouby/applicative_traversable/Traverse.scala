package com.ahoubouby.applicative_traversable

import cats.Applicative

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._ // for Applicative
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
  def traverse[G[_]: Applicative, A, B](inputs: F[A])(func: A => G[B]): G[F[B]]
  def sequence[G[_]: Applicative, B](inputs: F[G[B]]): G[F[B]] =
    traverse(inputs)(identity)
}

object Traverse extends App {
  def traverse[F[_]: Applicative, A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(Applicative[F].pure(List.empty[B])) { (a: A, acc: F[List[B]]) =>
      val fb: F[B] = f(a)
      Applicative[F].map2(fb, acc)(_ :: _)
    }

  implicit val traverseList = new Traverse[List] {
    override def traverse[G[_]: Applicative, A, B](fa: List[A])(f: A => G[B]): G[List[B]] =
      fa.foldRight(Applicative[G].pure(List.empty[B]))((a, acc) => Applicative[G].map2(f(a), acc)(_ :: _))
  }
  //-------------------------------------------------------------------------------//

  val hostnames                                = List("alpha.example.com", "beta.example.com", "gamma.demo.com")
  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60) // just for demonstration

  val allUptimes: Future[List[Int]] = hostnames.foldLeft(Future(List.empty[Int])) { (accum, host) =>
    val uptime = getUptime(host)
    for {
      accum <- accum
      uptime <- uptime
    } yield accum :+ uptime
  }

  def getOption(hostname: String) = Option(hostname.length)

  import cats.Applicative
  import cats.data.Validated
  import cats.instances.option._
  import cats.instances.vector._
  import cats.syntax.applicative._
  import cats.syntax.apply._

  def listTraverseWithApplicative[F[_]: Applicative, A, B](list: List[A])(f: A => F[B]): F[List[B]] =
    list.foldLeft(List.empty[B].pure[F])((accum, item) => (accum, f(item)).mapN(_ :+ _))

  def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] = listTraverseWithApplicative(list)(identity)

  type ErrorsOr[A] = Validated[List[String], A]
  import cats.instances.list._ // for Monoid

  def process(inputs: List[Int]): ErrorsOr[List[Int]] = listTraverseWithApplicative(inputs) { n =>
    if (n % 2 == 0) {
      Validated.valid(n)
    } else {
      Validated.invalid(List(s"$n is not even"))
    }
  }
  val res0         = Await.result(allUptimes, 1.second)
  val res1         = Await.result(Future.traverse(hostnames)(getUptime), 1.second)
  val res2         = Await.result(Future.sequence(hostnames.map(getUptime)), 1.second)
  val getAllUpTime = listTraverseWithApplicative(hostnames)(getOption)
  val res3         = process(List(2, 4, 6))
  val res4         = process(List(1, 2, 3))
  println(res0)
  println(res1)
  println(res2)
  println(getAllUpTime)
  println(listSequence(List(Vector(1, 2), Vector(3, 4))))
  println(res3)
  println(res4)

}
