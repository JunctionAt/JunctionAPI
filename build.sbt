import AssemblyKeys._

name := "Junction-API"

version := "1.0"

organization := "Junction"

resolvers += "Bukkit" at "http://repo.bukkit.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "org.bukkit" % "bukkit" % "1.7.2-R0.3",
  "org.json4s" %% "json4s-native" % "3.2.7",
  "org.scalaj" %% "scalaj-http" % "0.3.14",
  "com.rabbitmq" % "amqp-client" % "3.2.3",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "net.jodah" % "lyra" % "0.4.0"
)

assemblySettings