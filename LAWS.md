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
