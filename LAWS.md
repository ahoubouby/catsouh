#Laws

###Monoid 
For instance, the Monoid type class requires that combine be associative and empty be an identity element for combine. That means the following equalities should hold for any choice of x, y, and z.


```
combine(x, combine(y, z)) = combine(combine(x, y), z)
combine(x, id) = combine(id, x) = x
```

With these laws in place, functions parametrized over a Monoid can leverage them for say, performance reasons. A function that collapses a List[A] into a single A can do so with foldLeft or foldRight since combine is assumed to be associative, or it can break apart the list into smaller lists and collapse in parallel, such as

````scala
val list = List(1, 2, 3, 4, 5)
val (left, right) = list.splitAt(2)

// Imagine the following two operations run in parallel
val sumLeft = combineAll(left)
// sumLeft: Int = 3

val sumRight = combineAll(right)
// sumRight: Int = 12

// Now gather the results
val result = Monoid[Int].combine(sumLeft, sumRight)
// result: Int = 15
````

### Example instances

Cats provides many Semigroup instances out of the box such as Int (+) and String (++)
Instances for type constructors regardless of their type parameter such as List (++) and Set (union)
```scala
import cats.Semigroup
import cats.implicits._

Semigroup[Int]
// res0: cats.kernel.Semigroup[Int] = cats.kernel.instances.IntGroup@1619812c

Semigroup[String]
// res1: cats.kernel.Semigroup[String] = cats.kernel.instances.StringMonoid@6c1ab50f

Semigroup[List[Byte]]
// res2: cats.kernel.Semigroup[List[Byte]] = cats.kernel.instances.ListMonoid@214e39cb

Semigroup[Set[Int]]
// res3: cats.kernel.Semigroup[Set[Int]] = cats.kernel.instances.SetSemilattice@33285b3e

trait Foo
// defined trait Foo

Semigroup[List[Foo]]
```


# Exploiting laws: associativity
Since we know Semigroup#combine must be associative, we can exploit this when writing code against Semigroup. For instance, to sum a List[Int] we can choose to either foldLeft or foldRight since all that changes is associativity.
```scala
val leftwards = List(1, 2, 3).foldLeft(0)(_ |+| _)
// leftwards: Int = 6

val rightwards = List(1, 2, 3).foldRight(0)(_ |+| _)
// rightwards: Int = 6
```

#Laws
Conceptually, all type classes come with laws. These laws constrain implementations for a given type and can be exploited and used to reason about generic code.

---
#Functor

A Functor instance must obey two laws:
- Composition: Mapping with f and then again with g is the same as mapping once with the composition of f and g
  ```
   fa.map(f).map(g) = fa.map(f.andThen(g)) 
  ``
- Identity: Mapping with the identity function is a no-op
```scala
fa.map(x => x) = fa
``` 

--- 

# Applicative 
Applicative  must obey three laws:
            
- Associativity: No matter the order in which you product together three values, the result is isomorphic
  fa.product(fb).product(fc) ~ fa.product(fb.product(fc))
  With map, this can be made into an equality with fa.product(fb).product(fc) = fa.product(fb.product(fc)).map { case (a, (b, c)) => ((a, b), c) }
- Left identity: Zipping a value on the left with unit results in something isomorphic to the original value
  pure(()).product(fa) ~ fa
  As an equality: pure(()).product(fa).map(_._2) = fa
- Right identity: Zipping a value on the right with unit results in something isomorphic to the original value
  fa.product(pure(())) ~ fa
  As an equality: fa.product(pure(())).map(_._1) = fa
---
#Monad Laws: 
pure and flatMap must obey a set of laws that allow us to sequence opera􏰀ons freely without unintended glitches and side-effects:
- Le􏰁 iden􏰄ty: calling pure and transforming the result with func is the same as calling func:
```scala
pure(a).flatMap(func) == func(a)
```
- Right iden􏰄ty: passing pure to flatMap is the same as doing nothing:
```scala
m.flatMap(pure) == m
```
- Associa􏰄vity: flatMapping over two func􏰀ons f and g is the same as flatMapping over f and then flatMapping over g:
```scala
m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
```
