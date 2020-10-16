# SEMIGROUP 
Semigroup isn’t powerful enough for us to implement this function - namely, it doesn’t give us an identity or fallback value if the list is empty. We need a power expressive abstraction, which we can find in the Monoid type class.

This lifting and combining of Semigroups into Option is provided by Cats as Semigroup.combineAllOption.
```scala
Monoid.combineAll(lifted)
// res0: Option[cats.data.NonEmptyList[Int]] = Some(NonEmptyList(1, 2, 3, 4, 5, 6))
```
