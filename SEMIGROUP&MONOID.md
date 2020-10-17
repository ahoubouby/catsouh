# Semigroup 

If a type A can form a Semigroup it has an associative binary operation.
Associativity means the following equality must hold for any choice of x, y, and z.
```scala
trait Semigroup[A] {
def combine (x : A, y : A ): A 
}
``` 
Infix syntax is also available for types that have a Semigroup instance.
````scala
import cats.implicits._
// import cats.implicits._

1 |+| 2
// res3: Int = 3
````
# Monoid

Monoid extends the power of Semigroup by providing an additional empty value.
This empty value should be an identity for the combine operation, which means the following equalities hold for any choice of x
```scala
combine(x, empty) = combine(empty, x) = x
```
