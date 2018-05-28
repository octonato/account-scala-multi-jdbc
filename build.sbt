organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"


// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.12"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val postgres = "org.postgresql" % "postgresql" % "42.1.4"


lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false

lazy val `account` = (project in file("."))
  .aggregate(`account-api`, `account-impl`)

lazy val `account-api` = (project in file("account-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `account-impl` = (project in file("account-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      postgres,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`account-api`)
