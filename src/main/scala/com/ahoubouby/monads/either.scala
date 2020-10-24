package com.ahoubouby.monads

object either extends App {
  val either1: Either[String, Int] = Right(10)
  val either2: Either[String, Int] = Right(32)

  for {
    a <- either1
    b <- either2
  } yield a + b //// res0: scala.util.Either[String,Int] = Right(42)
  // =========== using cats ============ //
  import cats.syntax.either._
  val a = 3.asRight[String]
  // a: Either[String,Int] = Right(3)
  val b = 4.asRight[String]
  // b: Either[String,Int] = Right(4)
  val res1 = for {
    x <- a
    y <- b
  } yield x * x + y * y // res1: scala.util.Either[String,Int] = Right(25)

  def countPositive(nums: List[Int]) = nums.foldLeft(0.asRight[String]) { (accumulator, num) =>
    if (num > 0) { accumulator.map(_ + 1) }
    else {
      Left("Negative. Stopping!")
    }
  }

  countPositive(List(1, 2, 3)) // res2: Either[String,Int] = Right(3)

  Either.catchOnly[NumberFormatException]("foo".toInt)
  //res3:  Either[NumberFormatException,Int] = Left(java.lang.
  //     NumberFormatException: For input string: "foo")

  Either.catchNonFatal(sys.error("Badness"))

  //res4: : Either[Throwable,Nothing] = Left(java.lang.RuntimeException:
  //Badness)
  Either.fromTry(scala.util.Try("foo".toInt))
  // res9: Either[Throwable,Int] = Left(java.lang.NumberFormatException:For input string: "foo")
  Either.fromOption[String, Int](None, "Badness") // res10: Either[String,Int] = Left(Badness)
  // Transforming Eithers
  "Error".asLeft[Int].getOrElse(0)
  "Error".asLeft[Int].orElse(2.asRight[String])
  //res10: : Either[String,Int] = Right(2)
  -1.asRight[String].ensure("Must be non-negative!")(_ > 0) // res13: Either[String,Int] = Left(Must be non-negative!)
  "error".asLeft[Int].recover { case str: String => -1 }
  // res14: Either[String,Int] = Right(-1)
  "error".asLeft[Int].recoverWith { case str: String => Right(-1) }
  // res15: Either[String,Int] = Right(-1)

  // #There are leftMap and bimap methods to complement map:
  "foo".asLeft[Int].leftMap(_.reverse)
  // res16: Either[String,Int] = Left(oof)
  6.asRight[String].bimap(_.reverse, _ * 7)
  // res17: Either[String,Int] = Right(42)
  "bar".asLeft[Int].bimap(_.reverse, _ * 7) // res18: Either[String,Int] = Left(rab)
  // =================================== //
}
