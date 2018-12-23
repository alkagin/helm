
libraryDependencies ++= Seq(
  "io.argonaut"                %% "argonaut"          % "6.2.1",
  "org.typelevel"              %% "cats-free"         % "1.5.0",
  "org.typelevel"              %% "cats-effect"       % "1.1.0"
)

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.5" cross CrossVersion.binary)
