# SEMIGROUP 
Semigroup isn’t powerful enough for us to implement this function - namely, it doesn’t give us an identity or fallback value if the list is empty. We need a power expressive abstraction, which we can find in the Monoid type class.

This lifting and combining of Semigroups into Option is provided by Cats as Semigroup.combineAllOption.
```scala
Monoid.combineAll(lifted)
// res0: Option[cats.data.NonEmptyList[Int]] = Some(NonEmptyList(1, 2, 3, 4, 5, 6))
```
---

# Functor 
compose Functor => 
-  This approach will allow us to use composition without wrapping the value in question, 
- For example, if we need to call another function which requires a Functor and we want to use the composed Functor, we would have to explicitly pass in the composed instance during the function call or create a local implicit.
```scala
def needsFunctor[F[_]: Functor, A](fa: F[A]): F[Unit] = Functor[F].map(fa)(_ => ())

def foo: List[Option[Unit]] = {
  val listOptionFunctor = Functor[List].compose[Option]
  type ListOption[A] = List[Option[A]]
  needsFunctor[ListOption, Int](listOption)(listOptionFunctor)
}
```
- We can make this nicer at the cost of boxing with the Nested data type.

```scala
import cats.data.Nested
import cats.implicits._

val nested: Nested[List, Option, Int] = Nested(listOption)
// nested: cats.data.Nested[List,Option,Int] = Nested(List(Some(1), None, Some(2)))

nested.map(_ + 1)
// res2: cats.data.Nested[List,Option,Int] = Nested(List(Some(2), None, Some(3)))
```
-- The Nested approach, being a distinct type from its constituents, will resolve the usual way modulo possible SI-2712 issues (which can be addressed through partial unification), but requires syntactic and runtime overhead from wrapping and unwrapping.
# Applicative
Applicative extends Functor with an ap and pure method.
```scala
import cats.Functor

trait Applicative[F[_]] extends Functor[F] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def pure[A](a: A): F[A]

  def map[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)
}
```
- pure wraps the value into the type constructor 
#Applicatives compose
Like Functor, Applicatives compose. If F and G have Applicative instances, then so does F[G[_]].
```scala
import cats.data.Nested
import cats.implicits._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val x: Future[Option[Int]] = Future.successful(Some(5))
val y: Future[Option[Char]] = Future.successful(Some('a'))
val composed = Applicative[Future].compose[Option].map2(x, y)(_ + _)
// composed: scala.concurrent.Future[Option[Int]] = Future(<not completed>)

val nested = Applicative[Nested[Future, Option, *]].map2(Nested(x), Nested(y))(_ + _)
```
---

Foldable defines foldRight differently to foldLeft, in terms of the Eval
```scala
 def foldRight[A, B](fa: F[A], lb: Eval[B])
                     (f: (A, Eval[B]) => Eval[B]): Eval[B]
```
using Eval means folding is always stack safe, even when the collec􏰀on’s de- fault defini􏰀on of foldRight is not.
