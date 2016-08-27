name := """flash-job"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)
    .enablePlugins(BuildInfoPlugin)
    .settings(
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
