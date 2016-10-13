import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.{Word2VecModel, Word2Vec}
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by itawfik on 4/26/16.
 */
object ClusterDocuments {
  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[*]").setAppName("Word2Vect").set("spark.driver.host", "127.0.0.1").set("spark.cores.max", "10")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

//    val reviewData = sqlContext.read.parquet("src/main/resources/ParquetFiles/YelpReviews")
//    val convertToTupleArray = (s: String) => CreateVectorModel.removeStopWords(s.split("\\s+"))
//
//
//
//    sqlContext.udf.register("convert", convertToTupleArray)
//    reviewData.registerTempTable("reviewData")
//    val scrubbedData = sqlContext.sql("Select *, convert(content) as words from reviewData")
//
//    val model = Word2VecModel.load("WordVecModel")
//    val dataWithVector = model.transform(scrubbedData)
//    dataWithVector.write.parquet("dataWithVector")
    val computedTable = sqlContext.read.parquet("dataWithVector")
    val kmeansClusterer = new KMeans().setFeaturesCol("vector").setPredictionCol("cluster").setK(20)
    val kMeansModel = kmeansClusterer.fit(computedTable)
    kMeansModel.save("KmeansModel2")

    val clusteredData = kMeansModel.transform(computedTable)
    clusteredData.write.parquet("clusteredReviews2")
  }
}
