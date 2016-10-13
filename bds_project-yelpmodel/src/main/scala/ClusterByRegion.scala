
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by itawfik on 5/1/16.
 */
object ClusterByRegion {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName("Word2Vect").set("spark.driver.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val regions = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .load("yelp_JoinResult.csv")

    regions.registerTempTable("regions")
    regions.show()

    val reviewData = sqlContext.read.parquet("RestReviewClusters")
    reviewData.registerTempTable("reviews")

    val arraySum = new ArraySum
    sqlContext.udf.register("arraySum", arraySum)

    val aggregateByArea = sqlContext.sql("Select PUMACE10, NAME10 ,year, arraySum(vector) as allReviews from reviews left join regions on reviews.Restaurant = regions.YELPID group by PUMACE10, NAME10, year")
    val normailize = (a:Seq[Int]) => makePercent(a)
    aggregateByArea.registerTempTable("temp")
    sqlContext.udf.register("normalize", normailize)
    val restul = sqlContext.sql("Select PUMACE10, NAME10, year, allReviews, normalize(allReviews) as normalizedClusters from temp")
    //restul.write.parquet("clusteredByRegion")
    restul.coalesce(1).write
      .format("com.databricks.spark.csv")
      .option("delimiter", "|")
      .option("header", "true")
      .save("clusteredByRegion.csv")
  }

  def makePercent(array:Seq[Int]): Seq[Double] ={
    val sum = array.sum
    array.map(x => x.toDouble).map(x => BigDecimal(x/sum).setScale(6, BigDecimal.RoundingMode.HALF_UP).toDouble)
  }
}
