import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}


/**
* @ autthor - Sarthak Jain
*
* File to convert google places data from json to parquet format.
*/
object ReadGooglePlaces {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("GooglePlaces")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val path = "/home/cloudera/Documents/sarthak/sampleSparkPlacesData.json"
    val places = sqlContext.read.json(path)
    places.registerTempTable("place")

sqlContext.sql("Select * from place").drop("data").write.parquet("googlePlacesParaquet")
  }
}









