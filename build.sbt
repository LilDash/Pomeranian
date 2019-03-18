lazy val akkaHttpVersion = "10.1.3"
lazy val akkaVersion    = "2.5.14"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "0pla.net",
      scalaVersion    := "2.12.6",
      version         := "1.0.1"
    )),
    name := "pomeranian",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test,

      "mysql"             % "mysql-connector-java"  % "8.0.11",

      "com.typesafe.slick" %% "slick"               % "3.2.3",
//      "org.slf4j"         % "slf4j-nop"             % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp"      % "3.2.3",

      "com.typesafe"      % "config"                % "1.3.0",
//      "com.aliyun.oss"    % "aliyun-sdk-oss"        % "3.1.0",
      "ch.megard"         %% "akka-http-cors"       % "0.3.0",
//      "com.nulab-inc"     %% "scala-oauth2-core"    % "1.3.0",
//      "com.nulab-inc"     %% "akka-http-oauth2-provider" % "1.3.0",
      "io.igl"            %% "jwt"                  % "1.2.2",

      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.paulgoldbaum" %% "scala-influxdb-client" % "0.6.1"
    )

)
enablePlugins(JavaServerAppPackaging)
