name := "YelpCrawler"

version := "1.0"

scalaVersion := "2.11.8"
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalacOptions += "-target:jvm-1.7"
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

libraryDependencies += "com.github.melrief" %% "purecsv" % "0.0.6"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "0.1.2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1"
libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.1.0"
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "com.google.code.gson" % "gson" % "2.6.2"
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.2"
val json4sNative = "org.json4s" %% "json4s-native" % "{latestVersion}"
assemblyMergeStrategy in assembly := {
  case PathList("com", "esotericsoftware", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", xs @ _*)         => MergeStrategy.first
  case PathList("com", "google", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

