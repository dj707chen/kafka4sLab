// This issue is preventing me to use sbt-scoverage plugin: https://github.com/scoverage/sbt-scoverage/issues/319,
// It is related to Scala version 2.12.13 and due to the way that sbt-scoverage specify its dependencies.
// This could be fixed by upgrading to Scala 2.13.5, so I did that.
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
