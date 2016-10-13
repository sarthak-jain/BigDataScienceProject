import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by itawfik on 3/27/16.
 */
object GoogleDataConversion {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("YelpData")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val path = "/Users/islam/IdeaProjects/yelpmenu/PlacesData.json"
    val places = sqlContext.read.json(path)
    places.printSchema()
//    places.registerTempTable("place")
//
//sqlContext.sql("Select *, data.broker, " +
//      "data.buyer_commission," +
//      "data.clghgt," +
//      "data.company," +
//      "data.disabled_as_dupe_of," +
//      "data.exclusive," +
//      "data.exclusive_expires_on," +
//      "data.featured_end," +
//      "data.featured_start," +
//      "data.floored_order," +
//      "data.gross_commission," +
//      "data.lease_term_max," +
//      "data.lease_term_min," +
//      "data.open_house_last_tweeted," +
//      "data.scraped_area_id," +
//      "data.scraped_hood," +
//      "data.showing_instructions," +
//      "data.source_addr_house," +
//      "data.source_addr_street," +
//      "data.taxded," +
//      "data.unittype_for_editing from place").drop("data").write.parquet("streetEasy")
  }
}
