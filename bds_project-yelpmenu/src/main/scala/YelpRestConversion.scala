import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by itawfik on 3/13/16.
 */
object YelpRestConversion {
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
      .load("Restaurant/*")

    val cleanedTitles = df.withColumnRenamed("C0", "Restaurant")
        .withColumnRenamed("C0", "ID")
        .withColumnRenamed("C1", "name")
        .withColumnRenamed("C2", "address")
        .withColumnRenamed("C3", "phone")
        .withColumnRenamed("C4", "price")
//        .withColumnRenamed("C5", "reviewCount")
//        .withColumnRenamed("C6", "rating")
        .withColumnRenamed("C7", "category")
        .withColumnRenamed("C8", "openHour")
        .withColumnRenamed("C9","yelpURL")

    cleanedTitles.show()
    cleanedTitles.printSchema()
    //
    cleanedTitles.registerTempTable("Temp")


    sqlContext.udf.register("toDouble", (x:String) => convertToDouble(x))
    sqlContext.udf.register("toInt", (x:String) => convertToInt(x))

    sqlContext.sql("Select *, toInt(C5) as reviewCount, toDouble(C6) as rating from Temp").drop("C5").drop("C6").coalesce(1)
      .write.parquet("YelpRestaurants")




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



}
