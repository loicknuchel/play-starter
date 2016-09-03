name := """flash-job"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.2" % Test
)

play.sbt.routes.RoutesKeys.routesImport ++= Seq(
  "global.models.Page",
  "com.flashjob.domain.models.User",
  "com.flashjob.domain.models.JobOffer")

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)
    .enablePlugins(BuildInfoPlugin)
    .settings(
      scalacOptions ++= Seq(
        "-Xlint", // Enable or disable specific warnings: `_' for all, `-Xlint:help' to list
        //"-Xfatal-warnings", // Fail the compilation if there are any warnings.
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-encoding", "UTF-8", // Specify character encoding used by source files.
        "-unchecked", // Enable additional warnings where generated code depends on assumptions.
        "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
        "-Ywarn-dead-code", // Warn when dead code is identified. (N.B. doesn't work well with the ??? hole)
        "-Ywarn-numeric-widen", // Warn when numerics are widened.
        //"-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
        "-Xfuture", // Turn on future language features.
        "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are are unused.
        //"-Ywarn-unused-import", // Warn when imports are unused.
        "-Ydelambdafy:method", // Strategy used for translating lambdas into JVM code. (inline,method) default:inline
        "-Ybackend:GenBCode", // Choice of bytecode emitter. (GenASM,GenBCode) default:GenASM
        "-target:jvm-1.8" // Target platform for object files. All JVM 1.5 targets are deprecated. (jvm-1.5,jvm-1.6,jvm-1.7,jvm-1.8)
      ),
      buildInfoKeys := Seq[BuildInfoKey](
        name, version, scalaVersion, sbtVersion,
        "gitHash" -> new java.lang.Object(){
          override def toString(): String = {
            try {
              val extracted = new java.io.InputStreamReader(java.lang.Runtime.getRuntime().exec("git rev-parse --short HEAD").getInputStream())
              (new java.io.BufferedReader(extracted)).readLine()
            } catch {
              case t: Throwable => "get git hash failed"
            }
          }
        }.toString()
      ),
      buildInfoPackage := "global",
      buildInfoOptions := Seq(BuildInfoOption.BuildTime)
    )

routesGenerator := InjectedRoutesGenerator
