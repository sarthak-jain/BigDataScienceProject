package main.scala

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import com.databricks.spark.csv;


object Reviews {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Sietesoles\\Downloads\\hadoop-common-2.2.0-bin-master\\hadoop-common-2.2.0-bin-master\\");
    val config = new SparkConf().setMaster("local[4]").setAppName("YelpReviews");
    val sc = new SparkContext(config);
    val sqlContext = new SQLContext(sc);
    
    val tables: DataFrame = sqlContext.read.parquet("ParquetTables/YelpReviews");
    
//    tables.show();
    
    tables.registerTempTable("reviews");
    val RestaurantMinMaxDate: DataFrame = sqlContext.sql("Select Restaurant, Min(datePublished) as Min_date, Max(datePublished) as Max_date from reviews group by Restaurant");
    
    RestaurantMinMaxDate.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("RestaurantMinMax.csv");

  }
}