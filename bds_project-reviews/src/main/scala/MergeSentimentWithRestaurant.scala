package main.scala

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import com.databricks.spark.csv;
import com.databricks.spark.csv.CsvContext;

object MergeSentimentWithRestaurant {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master\\");
    val config = new SparkConf().setMaster("local[*]").setAppName("YelpReviews").set("spark.default.parallelism", "6");
    val sc = new SparkContext(config);
    val sqlContext = new SQLContext(sc);
    
    val sentiment = sqlContext.csvFile("yelp_review_all.csv");
    val dataRegionsTrimmed = sqlContext.csvFile("RestaurantAllStarsWithYearAndRegion.csv/RestaurantAllStarsWithYearAndRegion.csv");
    val dataSentiment = dataRegionsTrimmed.join(sentiment, sentiment("businessId") === dataRegionsTrimmed("Restaurant"));
   
    dataSentiment.registerTempTable("dataSentiment");
    
    val dataRegionsSentiment = sqlContext.sql("Select Restaurant, Year, OneStar, TwoStar, ThreeStar, FourStar, FiveStar, PUMACE10, pos_word, neg_word, total_words, reviewCount, rating, category from dataSentiment group by Restaurant, Year, OneStar, TwoStar, ThreeStar, FourStar, FiveStar, PUMACE10, pos_word, neg_word, total_words, reviewCount, rating, category");
    dataRegionsSentiment.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("Restaurants2.csv");

  }
  
}