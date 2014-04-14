import sbtassembly.Plugin.AssemblyKeys._

name := "Junction-API"

version := "1.0"

organization := "Junction"

resolvers += "Bukkit" at "http://repo.bukkit.org/content/groups/public/"

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5",
  "org.bukkit" % "bukkit" % "1.7.8-R0.1-SNAPSHOT" % "provided",
  "org.json4s" %% "json4s-native" % "3.2.8",
  "org.scalaj" %% "scalaj-http" % "0.3.14",
  "com.rabbitmq" % "amqp-client" % "3.2.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "net.jodah" % "lyra" % "0.4.0"
)

assemblySettings

//assemblyOption in assembly ~= { _.copy(includeScala = false) }