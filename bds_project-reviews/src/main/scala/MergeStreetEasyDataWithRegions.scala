package main.scala

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import com.databricks.spark.csv;
import com.databricks.spark.csv.CsvContext;

object MergeStreetEasyDataWithRegions {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master\\");
    val config = new SparkConf().setMaster("local[*]").setAppName("YelpReviews").set("spark.default.parallelism", "6");
    val sc = new SparkContext(config);
    val sqlContext = new SQLContext(sc);
    
    val streetEasyData = sqlContext.csvFile("streetEasyUnique.csv");
    val regions = sqlContext.csvFile("streetEasy_JoinResult.csv");
    
    val streetEasy = streetEasyData.join(regions, regions("STREETEASYID") === streetEasyData("id"));
    
    streetEasy.registerTempTable("streetEasy");
    
    val finalStreetEasy = sqlContext.sql("Select id, price, bedrooms, PUMACE10, LONGITUDE, LATITUDE from streetEasy group by id, price, bedrooms, PUMACE10, LONGITUDE, LATITUDE");
    
    finalStreetEasy.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("StreetEasyWithRegions.csv");

  }
  
}