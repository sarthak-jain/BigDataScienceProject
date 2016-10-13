import java.sql.Date
import java.time.LocalDate

import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.DateType
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.types

/**
 * Created by itawfik on 4/26/16.
 */
object ClusterArrays {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName("CreateVectors").set("spark.driver.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val clusteredData = sqlContext.read.parquet("clusteredReviews2")

    val getYear = (x:Date) =>  if(x == null)  LocalDate.MAX.getYear else x.toLocalDate.getYear
    val vectors = (x:Int, y:Int) => vectorize(x,y)
    sqlContext.udf.register("getYear", getYear)
    sqlContext.udf.register("vectorize", vectors)
    clusteredData.registerTempTable("clustered")

    val intermediate = sqlContext.sql("Select Restaurant, getYear(datePublished) as year,cluster, count(cluster) as count  , vectorize(cluster, count(cluster)) as clusterVector" +
      " from clustered group by Restaurant, getYear(datePublished),cluster")

    intermediate.registerTempTable("clustered")
    val arraySum = new ArraySum
    sqlContext.udf.register("arraySum", arraySum)
    val finalResult = sqlContext.sql("Select Restaurant, year, arraySum(clusterVector) as vector from clustered group by Restaurant, year")
    finalResult.write.parquet("RestReviewClusters")
  }


  def vectorize(position:Int, count:Int): Array[Int] ={
    val x = Array.fill[Int](20)(0)
    x.update(position,count)
    x
  }



}
