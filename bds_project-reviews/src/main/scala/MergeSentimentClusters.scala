package main.scala

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import com.databricks.spark.csv;
import com.databricks.spark.csv.CsvContext;

object MergeSentimentClusters {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master\\");
    val config = new SparkConf().setMaster("local[*]").setAppName("YelpReviews");
    val sc = new SparkContext(config);
    val sqlContext = new SQLContext(sc);
    
    val tables: DataFrame = sqlContext.csvFile("sentiment2010_2014.csv");
    
//    tables = tables.filter(tables("TotalNumReviews") > 50);
    
    val clusters: DataFrame = sqlContext.csvFile("clusters2010_2014.csv");
    
    val merged = tables.join(clusters, tables("Region") === clusters("PUMACE10") && tables("Year") === clusters("year"));
    
    val newMerged = merged.drop("year").drop("PUMACE10");
    
    newMerged.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("clustersWithSentiment2010_2014");

  }
}