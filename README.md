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

# Applicative 
#### Applicatives for effect management

If we view Functor as the ability to work with a single effect, Applicative encodes working with multiple independent effects. Between product and map, we can take two separate effectful values and compose them. From there we can generalize to working with any N number of independent effects.
```scala
import cats.Applicative

def product3[F[_]: Applicative, A, B, C](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] = {
  val F = Applicative[F]
  val fabc = F.product(F.product(fa, fb), fc)
  F.map(fabc) { case ((a, b), c) => (a, b, c) }
}
```
---
# Foldable and Traverse
- Foldable abstracts the familiar foldLeft and foldRight opera􏰀ons.
- Traverse is a higher-level abstrac􏰀on that uses Applicatives to it-
erate with less pain than folding.
Note: foldLeft and foldRight are equivalent if our binary opera􏰀on is commuta􏰀ve.

### foldable with cats 
Cats’ Foldable abstracts foldLeft and foldRight into a type class.
In- stances of Foldable define these two methods and inherit a host of derived methods.
Cats provides out-of-the-box instances of Foldable for a handful of Scala data types: List, Vector, Stream, and Option.
```scala
import cats.Foldable
import cats.instances.list._ // for Foldable
val ints = List(1, 2, 3)
Foldable[List].foldLeft(ints, 0)(_ + _)
import cats.instances.option._ // for Foldable val maybeInt = Option(123)
Foldable[Option].foldLeft(maybeInt, 10)(_ * _)
// res3: Int = 1230
```
we can compose Foldables to support deep traversal of nested sequences:

```scala
import cats.instances.vector._ // for Monoid
val ints = List(Vector(1, 2, 3), Vector(4, 5, 6))
(Foldable[List] compose Foldable[Vector]).combineAll(ints)
// res15: Int = 21
```
---
# Traverse
foldLeft and foldRight are flexible itera􏰀on methods but they require us to do a lot of work to define accumulators and combinator func􏰀ons. The Traverse type class is a higher level tool that leverages Applicatives to provide a more convenient, more lawful, pa􏰃ern for itera􏰀on.
Cats provides instances of Traverse for List, Vector, Stream, Option, Ei- ther, and a variety of other types. We can summon instances as usual using Traverse.apply and use the traverse and sequence methods as described in the previous sec􏰀onction:
```scala
import cats.Traverse
import cats.instances.future._ // for Applicative import cats.instances.list._ // for Traverse
val totalUptime: Future[List[Int]] = Traverse[List].traverse(hostnames)(getUptime)
Await.result(totalUptime, 1.second)
// res1: List[Int] = List(1020, 960, 840)
val numbers = List(Future(1), Future(2), Future(3))
val numbers2: Future[List[Int]] =
Traverse[List].sequence(numbers)
Await.result(numbers2, 1.second)
// res3: List[Int] = List(1, 2, 3)
```
There are also syntax versions of the methods, imported via cats.syntax.traverse
```scala
import cats.syntax.traverse._ // for sequence and traverse Await.result(hostnames.traverse(getUptime), 1.second)
// res4: List[Int] = List(1020, 960, 840)
Await.result(numbers.sequence, 1.second)
// res5: List[Int] = List(1, 2, 3)
```
---
#Monad (A monad is a mechanism for sequencing computa􏰄ons.)
Monad extends the Applicative type class with a new function flatten. Flatten takes a value in a nested context (eg. F[F[A]] where F is the context) and “joins” the contexts together so that we have a single context (ie. F[A]).
The name flatten should remind you of the functions of the same name on many classes in the standard library.
# FlatMap
flatMap is often considered to be the core function of Monad, and Cats follows this tradition by providing implementations of flatten and map derived from flatMap and pure.
---
# MonadError
Cats provides an addi􏰀onal type class called MonadError that abstracts over Either-like data types that are used for error handling. MonadError provides extra opera􏰀ons for raising and handling errors.
---
#Monad Eval
cats.Eval is a monad that allows us to abstract over different models of eval- ua􏰄on. We typically hear of two such models: eager and lazy. Eval throws in a further dis􏰀nc􏰀on of whether or not a result is memoized.
### what's Eager, Lazy, Memoized.
Eager computa􏰀ons happen immediately whereas lazy computa􏰀ons happen on access. Memoized computa􏰀ons are run once on first access, a􏰁er which the results are cached.
## Eval’s Models of Evalua􏰀on

Eval has three subtypes: Now, Later, and Always. We construct these with three constructor methods, which create instances of the three classes and return them typed as Eval:
````scala
import cats.Eval
val now = Eval.now(math.random + 1000)
// now: cats.Eval[Double] = Now(1000.3579280134779)
val later = Eval.later(math.random + 2000)
// later: cats.Eval[Double] = cats.Later@359ba169
val always = Eval.always(math.random + 3000)
// always: cats.Eval[Double] = cats.Always@6765a7a7
````
