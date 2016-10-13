import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by islam on 4/9/16.
 */
object Mapping {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]")
      .setAppName("YelpData")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)




////
//    val healthMapping = sqlContext.read.parquet("DepOfHealth").repartition()
//    healthMapping.registerTempTable("health")
//    val yelpMapping = sqlContext.read.parquet("YelpMapping")
//    yelpMapping.registerTempTable("yelp")
//
//    healthMapping.printSchema()
//    yelpMapping.show()
//    sqlContext.sql(
//      "Select health.CAMIS, yelp.YelpId, yelp.DBA, yelp.BORO, yelp.STREET, yelp.BUILDING, yelp.ZIPCODE, yelp.link from health left join yelp on health.DBA = yelp.DBA and health.BORO = yelp.BORO and health.STREET = yelp.STREET and yelp.BUILDING = health.BUILDING and yelp.ZIPCODE = health.ZIPCODE where yelp.YelpId IS NOT NULL")
//      .dropDuplicates()
//      .write.parquet("mappingTable")

    val mappingTable = sqlContext.read.parquet("mappingTable")
    mappingTable.show()
    println(mappingTable.count())

    //Get health department related data
//    val df = sqlContext
//      .read
//      .format("com.databricks.spark.csv")
//      .option("delimiter", ",")
//      .option("header", "true")
//      .load("DOHMH.csv")
//
//
//
//    df.registerTempTable("health")
//
//    val health = sqlContext
//      .sql("Select CAMIS, DBA, BORO, STREET, BUILDING, ZIPCODE FROM health GROUP BY CAMIS, DBA, BORO, STREET, BUILDING, ZIPCODE")
//    health.write.parquet("DepOfHealth")
//    health.registerTempTable("health")

    //Get Yelp data
    //Get health department related data
//    val yelp = sqlContext
//      .read
//      .format("com.databricks.spark.csv")
//      .option("delimiter", "|")
//      .load("IDMAPPING/*")
//
//    val yelpCleaned = yelp
//      .withColumnRenamed("C0", "YelpId")
//      .withColumnRenamed("C1", "link")
//      .withColumnRenamed("C2", "DBA")
//      .withColumnRenamed("C3", "BORO")
//      .withColumnRenamed("C4", "STREET")
//      .withColumnRenamed("C5", "BUILDING")
//      .withColumnRenamed("C6", "ZIPCODE").coalesce(20)
////
//    yelpCleaned.write.parquet("YelpMapping")

    //yelpCleaned.registerTempTable("yelp")



    //Join the data
//    val health = sqlContext
//      .sql("Select CAMIS, DBA, BORO, STREET, BUILDING, ZIPCODE FROM health GROUP BY CAMIS, DBA, BORO, STREET, BUILDING, ZIPCODE")

      //sqlContext.sql("Select * from health left join yelp where health.DBA = yelp.DBA and health.BORO = yelp.BORO and health.STREET = yelp.STREET and yelp.BUILDING = health.BUILDING and yelp.ZIPCODE = health.ZIPCODE").show()
  }

}
