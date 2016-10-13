import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.functions.{stddev, avg, count}

/**
 * Created by itawfik on 5/2/16.
 */
object CensusData {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName("Census").set("spark.driver.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val numberize = (x:String) => if(x.length == 0) Option.empty else Some(x.toInt)
    val getDate = (x:String) => x.substring(0,4).toInt
    sqlContext.udf.register("num", numberize)
    sqlContext.udf.register("getDate", getDate)

    val censusData = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("/Users/itawfik/IdeaProjects/YelpModel/AmericanCommunitySurveyCensusDataTrimmed_ss14hny.csv")
      .select("SERIALNO", "PUMA10", "BDSP", "GRNTP")
      .where("PUMA10 != -0009")
      .where("PUMA10 is not null")
      .where("GRNTP is not null")


    censusData.registerTempTable("census")

    val clean = sqlContext.sql("Select getDate(SERIALNO) as year ,num(PUMA10) as cleanedPuma ,num(BDSP) as cleanedBDSP, num(GRNTP) as cleanedGRNTP from census")
    clean.registerTempTable("clean")

    val nullsRemoved = sqlContext.sql("Select * from clean where cleanedPuma is not null and cleanedBDSP is not null and cleanedGRNTP is not null")
    nullsRemoved.registerTempTable("fullData")

    val withStats = nullsRemoved
      .groupBy("year", "cleanedPuma", "cleanedBDSP")
      .agg(stddev("cleanedGRNTP"), avg("cleanedGRNTP"), count("cleanedGRNTP"))
      .withColumnRenamed("stddev_samp(cleanedGRNTP,0,0)", "standardDeviation")
      .withColumnRenamed("avg(cleanedGRNTP)", "average")
      .withColumnRenamed("count(cleanedGRNTP)", "counter")

    withStats.registerTempTable("stats")

    val ungroupedWithSats = sqlContext.sql("Select fullData.year, fullData.cleanedPuma, fullData.cleanedBDSP, cleanedGRNTP, standardDeviation, average, " +
      "counter from fullData left join stats on fullData.year = stats.year and fullData.cleanedPuma = stats.cleanedPuma and fullData.cleanedBDSP = stats.cleanedBDSP")

    ungroupedWithSats.registerTempTable("temp")

    val twoDeviations = (rent:Int,stdDeviation:Double,count:Int, average:Double) => isWithing2Stdev(rent,stdDeviation,count, average)
    sqlContext.udf.register("twoDeviations", twoDeviations)

    sqlContext.sql("Select *, twoDeviations(cleanedGRNTP,standardDeviation,counter,average) as include from temp ")
      .where("include = true")
      .select("year","cleanedPuma","cleanedBDSP","cleanedGRNTP")
      .groupBy("year","cleanedPuma","cleanedBDSP")
      .agg(avg("cleanedGRNTP")).coalesce(1).write.format("com.databricks.spark.csv").option("delimiter", "|").option("header","true").save("census")



  }

  def isWithing2Stdev(rent:Int,stdDeviation:Double,count:Int, average:Double): Boolean ={
    if(count <= 1) return false
    if(rent == average) return true
    val upperBound = average + stdDeviation * 2
    val lowerBound = average - stdDeviation * 2

    if(rent <= upperBound && rent >= lowerBound)  true else  false
  }

}
