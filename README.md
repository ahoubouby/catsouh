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
#Laws
Conceptually, all type classes come with laws. These laws constrain implementations for a given type and can be exploited and used to reason about generic code.


