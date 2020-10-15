name := "catsouh"

version := "0.1"

scalaVersion := "2.13.3"
scalacOptions += "-Ypartial-unification"
libraryDependencies ++= Seq(
   "org.typelevel" %% "cats-core" % "2.1.1"
)
