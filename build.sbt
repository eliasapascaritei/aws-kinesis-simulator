name := "kinesis"
 
version := "1.0" 
      
lazy val `kinesis` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.3"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project!")
}

scalacOptions := Seq(
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Wdead-code",
  "-Wunused:imports",
  "-Ymacro-annotations"
)

libraryDependencies ++= Seq(ws, specs2 % Test , guice )

libraryDependencies ++= Seq(
  "org.mongodb.scala"   %% "mongo-scala-driver"     % "4.1.1",
  "com.typesafe"         % "config"                 % "1.4.1",
  "com.typesafe.play"   %% "play-json"              % "2.9.2"
)