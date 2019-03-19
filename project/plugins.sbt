resolvers += Resolver.url(
  "tpolecat-sbt-plugin-releases",
    url("http://dl.bintray.com/content/tpolecat/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

addSbtPlugin("io.verizon.build" % "sbt-rig"         % "5.0.39")

// docs
addSbtPlugin("com.typesafe"     % "sbt-mima-plugin" % "0.1.14")
addSbtPlugin("org.tpolecat"     % "tut-plugin" % "0.6.7")
