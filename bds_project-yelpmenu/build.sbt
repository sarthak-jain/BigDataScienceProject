name := "YelpCrawler"

version := "1.0"

scalaVersion := "2.11.8"
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "com.github.melrief" %% "purecsv" % "0.0.6"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "0.1.2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1"
libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.1.0"
val json4sNative = "org.json4s" %% "json4s-native" % "{latestVersion}"
