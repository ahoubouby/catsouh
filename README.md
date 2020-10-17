# Type classes

Type classes are a powerful tool used in functional programming to enable ad-hoc polymorphism, more commonly known as overloading.
 Where many object-oriented languages leverage subtyping for polymorphic code, functional programming tends towards a combination of parametric polymorphism (think type parameters, like Java generics) and ad-hoc polymorphism.
# Monoid 
The name Monoid is taken from abstract algebra which specifies precisely this kind of structure.

```scala
trait Monoid[A] {
     def empty: A
     def combine(x: A, y: A): A
   } 
```
### Implementation for Int
````scala
val intAdditionMonoid: Monoid[Int] = new Monoid[Int] {
  def empty: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}
````

then we can write it like that's
````scala
  def combineAll[A](list: List[A], A: Monoid[A]): A = list.foldRight(A.empty)(A.combine)
````
 One of the most powerful features of type classes is the ability to do this kind of derivation automatically. We can do this through Scala’s implicit mechanism.
````scala
object Demo { // needed for tut, irrelevant to demonstration
  final case class Pair[A, B](first: A, second: B)

  object Pair {
    implicit def tuple2Instance[A, B](implicit A: Monoid[A], B: Monoid[B]): Monoid[Pair[A, B]] =
      new Monoid[Pair[A, B]] {
        def empty: Pair[A, B] = Pair(A.empty, B.empty)

        def combine(x: Pair[A, B], y: Pair[A, B]): Pair[A, B] =
          Pair(A.combine(x.first, y.first), B.combine(x.second, y.second))
      }
  }
}
````

---

#Applicative and Traversable Functors
```scala
import scala.concurrent.{ExecutionContext, Future}

def traverseFuture[A, B](as: List[A])(f: A => Future[B])(implicit ec: ExecutionContext): Future[List[B]] =
  Future.traverse(as)(f)
```
- traverseFuture takes a List[A] and for each A in the list applies the function f to it,
- f is often referred to as an effectful function, where the Future effect is running the computation concurrently, presumably on another thread. 
####Functor
- Functor is a type class that abstracts over type constructors that can be map‘ed over. Examples of such type constructors are List, Option, and Future.
```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

// Example implementation for Option
implicit val functorForOption: Functor[Option] = new Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
    case None    => None
    case Some(a) => Some(f(a))
  }
}
```
# Functors for effect management
- The F in Functor is often referred to as an “effect” or “computational context.” Different effects will abstract away different behaviors with respect to fundamental functions like map

# Functors compose

- If you’ve ever found yourself working with nested data types such as Option[List[A]] or List[Either[String, Future[A]]] and tried to map over it, you’ve most likely found yourself doing something like _.map(_.map(_.map(f))). As it turns out, Functors compose, which means if F and G have Functor instances, then so does F[G[_]].
- Such composition can be achieved via the Functor#compose method.
