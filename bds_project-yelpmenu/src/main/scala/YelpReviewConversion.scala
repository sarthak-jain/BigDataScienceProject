import java.sql.Date

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by itawfik on 3/13/16.
 */
object YelpReviewConversion {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]")
      .setAppName("YelpData")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val df = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .option("inferSchema", "true")
      .load("Review/*")

    df.show()

    val cleanedTitles = df.withColumnRenamed("C0", "Restaurant")
        .withColumnRenamed("C0", "ID")
//        .withColumnRenamed("C1", "reviewIdx")
        .withColumnRenamed("C2", "author")
        .withColumnRenamed("C3", "authorId")
//        .withColumnRenamed("C4", "ratingValue")
//        .withColumnRenamed("C5", "datePublished")
        .withColumnRenamed("C6", "content")
//        .withColumnRenamed("C7", "useful")
//        .withColumnRenamed("C8", "funny")
//        .withColumnRenamed("C9","cool")
//
//    cleanedTitles.show()
//    cleanedTitles.printSchema()
//    //
    cleanedTitles.registerTempTable("Temp")
//
//
    sqlContext.udf.register("toDouble", (x:String) => convertToDouble(x))
    sqlContext.udf.register("toInt", (x:String) => convertToInt(x))
    sqlContext.udf.register("toDate", (x:String) => convertToDate(x))
//
    sqlContext.sql("Select *, toInt(C1) as reviewIdx, toDouble(C4) as ratingValue, toDate(C5) as datePublished, toInt(C7) as useful, toInt(C8) as funny, toInt(C9) as cool from Temp")
      .drop("C1")
      .drop("C4")
      .drop("C7")
      .drop("C8")
      .drop("C9")
      .drop("C5")
      .coalesce(15)
      .write.parquet("YelpReviews")




  }

  def convertToDouble(value:String): Option[Double]={
    try{
      Some(value.trim.toDouble)
    }catch{
      case e: Exception => None
    }
  }

  def convertToInt(value:String): Option[Int]={
    try{
      Some(value.trim.toInt)
    }catch{
      case e: Exception => None
    }
  }

  def convertToDate(value:String): Option[java.sql.Date]={
    try{
      val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
      Some(Date.valueOf(value))
    }catch{
      case e: Exception => None
    }
  }



}
