package com.ahoubouby.types_classes
import cats.Semigroup
object entryPoint {
  def main(args: Array[String]): Unit = {
    import cats.Monoid

    implicit val intAdditionSemiGroup: Semigroup[Int] = (x: Int, y: Int) => x + y

    val x = 1
    val y = 2
    val z = 3

//    Semigroup[Int].combine(x, y) // res0: Int = 3
//
//    Semigroup[Int].combine(x, Semigroup[Int].combine(y, z)) // res1: Int = 6
//
//    Semigroup[Int].combine(Semigroup[Int].combine(x, y), z)

    // use infix syntax
    import cats.implicits._

    1 |+| 2

    val map1   = Map("hello" -> 1, "world" -> 1)
    val map2   = Map("hello" -> 2, "cats" -> 3)
    val result = Semigroup[Map[String, Int]].combine(map1, map2)
    println(result) //res4: Map[String,Int] = Map(hello -> 3, cats -> 3, world -> 1)
    val result2 = map1 |+| map2
    println(result2)
    def optionCombine[A: Semigroup](a: A, opt: Option[A]): A = opt.map(a |+| _).getOrElse(a)

    def mergeMap[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] = lhs.foldLeft(rhs) {
      case (acc, (k, v)) => acc.updated(k, optionCombine(v, acc.get(k)))
    }

    val xm1 = Map('a' -> 1, 'b' -> 2)
    // xm1: scala.collection.immutable.Map[Char,Int] = Map(a -> 1, b -> 2)

    val xm2 = Map('b' -> 3, 'c' -> 4)
    // xm2: scala.collection.immutable.Map[Char,Int] = Map(b -> 3, c -> 4)

    val xx = mergeMap(xm2, xm1)
    // x: Map[Char,Int] = Map(b -> 5, c -> 4, a -> 1)

    val ym1 = Map(1 -> List("hello"))
    // ym1: scala.collection.immutable.Map[Int,List[String]] = Map(1 -> List(hello))

    val ym2 = Map(2 -> List("cats"), 1 -> List("world"))
    // ym2: scala.collection.immutable.Map[Int,List[String]] = Map(2 -> List(cats), 1 -> List(world))

    val yy = mergeMap(ym2, ym1)
    // y: Map[Int,List[String]] = Map(2 -> List(cats), 1 -> List(hello, world))
    println(yy)
    println(xx)

    // ============================ Monoid ============================ //

    implicit val intAdditionMonoid: Monoid[Int] = new Monoid[Int] {
      def empty: Int                   = 0
      def combine(x: Int, y: Int): Int = x + y
    }

    val monoidx = 1

    Monoid[Int].combine(monoidx, Monoid[Int].empty) // // res0: Int = 1
    Monoid[Int].combine(Monoid[Int].empty, monoidx) // res1: Int = 1

    def combineAll[A: Monoid](as: List[A]): A = as.foldRight(Monoid[A].empty)(Monoid[A].combine)

    val combineAllVal  = combineAll(List(1, 2, 3))
    val combineAll1Val = Monoid[Int].combineAll(List(1, 2, 3))
    assert(combineAll1Val == combineAllVal, "something error in the assertion")
    // same for tuple
    val ListOfMap  = List(Map('a' -> 1), Map('a' -> 2, 'b' -> 3), Map('b' -> 4, 'c' -> 5))
    val someOfPair = combineAll(ListOfMap)
    //Second way to do it
    Monoid[Map[Char, Int]].combineAll(ListOfMap)
    println(someOfPair)
    // ================================================================ //

  }
}
