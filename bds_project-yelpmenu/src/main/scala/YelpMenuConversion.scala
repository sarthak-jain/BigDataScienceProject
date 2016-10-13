import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.functions.{lit, udf}

/**
 * Created by itawfik on 3/13/16.
 */
object YelpMenuConversion {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]")
      .setAppName("YelpData")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val df = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("delimiter", "|")
      .load("menuFinal/*")

    val cleanedTitles = df.withColumnRenamed("C0", "Restaurant")
        .withColumnRenamed("C1", "ID")
        .withColumnRenamed("C2", "Plate_Type")
        .withColumnRenamed("C3", "Menu_Item")
        .withColumnRenamed("C4", "p")
        .withColumnRenamed("C5", "Review_Count")

    cleanedTitles.registerTempTable("Temp")


    sqlContext.udf.register("toDouble", (x:String) => priceWrapper(x))
    sqlContext.sql("Select *, toDouble(p) as Price from Temp").drop("p").coalesce(1).write.parquet("menu")




  }

  def priceWrapper(value:String): Option[Double]={
    try{
      Some(value.trim.toDouble)
    }catch{
      case e: Exception => None
    }
  }


}
