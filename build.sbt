name := "leaker"

version := "1.0.0"

javacOptions += "-g"

scalaVersion := "2.11.4"

scalacOptions += "-unchecked"

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2"

libraryDependencies += "org.javassist" % "javassist" % "3.18.2-GA"

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.19.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

packageOptions in (Compile, packageBin) +=
  Package.ManifestAttributes( "Agent-Class" -> "org.leaker.LeakerAgent", "Can-Redefine-Classes" -> "true",
    "Can-Retransform-Classes" -> "true", "Premain-Class" -> "org.leaker.LeakerAgent")
