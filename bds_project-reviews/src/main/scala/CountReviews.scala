package main.scala

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import com.databricks.spark.csv;

object CountReviews {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master\\");
    val config = new SparkConf().setMaster("local[*]").setAppName("YelpReviews");
    val sc = new SparkContext(config);
    val sqlContext = new SQLContext(sc);
    
    val tables: DataFrame = sqlContext.read.parquet("ParquetTables/YelpReviews");
    
    tables.show();
//    tables.printSchema();
    
    tables.registerTempTable("reviews");
    
    sqlContext.udf.register("getYear", (x:String) => x.subSequence(0, 4).toString());

    val sqlcommand = "Select Restaurant, year(datePublished) as Year, sum(if(ratingValue=1,1,0)) as OneStar, " +
    "sum(if(ratingValue=2,1,0)) as TwoStar, sum(if(ratingValue=3,1,0)) as ThreeStar, "+
    "sum(if(ratingValue=4,1,0)) as FourStar, sum(if(ratingValue=5,1,0)) as FiveStar from reviews group by Restaurant, year(datePublished)";
    
    
    val restaurantStarsDataFrame: DataFrame = sqlContext.sql(sqlcommand);
    
    restaurantStarsDataFrame.show();
    
    restaurantStarsDataFrame.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("RestaurantAllStarsWithYear.csv");

  }
  
}