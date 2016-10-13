import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.functions.{lit, udf}

/**
 * Created by itawfik on 3/13/16.
 */
object FileConverter {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("YelpData")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val df = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("delimiter", "|")
      .load("RawData/*")

    val cleanedTitles = df.withColumnRenamed("C0", "Restaurant")
        .withColumnRenamed("C1", "ID")
        .withColumnRenamed("C2", "Plate_Type")
        .withColumnRenamed("C3", "Menu_Item")
        .withColumnRenamed("C4", "Price")
        .withColumnRenamed("C5", "Review_Count")

    cleanedTitles.registerTempTable("Temp")


    sqlContext.udf.register("toDouble", (x:String) => priceWrapper(x))
    val x = sqlContext.sql("Select *, toDouble(Price) as Clean_Price from Temp").drop("Price").show(200)




  }

  def priceWrapper(value:String): Option[Double]={
    try{
      Some(value.trim.toDouble)
    }catch{
      case e: Exception => None
    }
  }

//  /**
//   * We want relativly large partion sizes for faster access, 64MB is a small hdfs block so we will try to partiion to
//   * approximatly that size
//   * @param recordCount
//   * @param directoryLocation
//   * @return
//   */
//  def estimateNumPartions(recordCount:Int, directoryLocation:String): Int ={
//
//  }


}
