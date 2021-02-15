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
  "com.typesafe"                            % "config"                 % "1.4.1",
  "com.typesafe.play"                      %% "play-json"              % "2.9.2"
)

val mongockVersion = "4.1.17"
libraryDependencies ++= Seq(
  "org.mongodb"                            %  "mongo-java-driver"       % "3.12.7",
  "org.mongodb.scala"                      %% "mongo-scala-driver"     % "2.9.0",
  "ch.rasc"                                 % "bsoncodec"              % "1.0.1",
  "com.github.cloudyrock.mongock"           % "mongock-bom"            % mongockVersion,
  "com.github.cloudyrock.mongock"           % "mongock-standalone"     % mongockVersion,
  "com.github.cloudyrock.mongock"           % "mongodb-v3-driver"      % mongockVersion
)