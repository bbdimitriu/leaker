name := "leaker"

version := "1.0.0"

javacOptions += "-g"

scalaVersion := "2.11.1"

scalacOptions += "-unchecked"

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2"

libraryDependencies += "org.aspectj" % "aspectjrt" % "1.8.1"

libraryDependencies += "org.javassist" % "javassist" % "3.18.2-GA"

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.19.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
