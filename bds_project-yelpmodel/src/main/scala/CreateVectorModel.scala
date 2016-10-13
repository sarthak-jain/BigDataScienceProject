import org.apache.spark.ml.feature.{Word2VecModel, Word2Vec}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.mutable.HashSet
import scala.io.Source

/**
 * Created by itawfik on 4/24/16.
 */
object CreateVectorModel {
  val stopWords = loadStopWords()

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[4]").setAppName("Word2Vect").set("spark.driver.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val reviewData = sqlContext.read.parquet("src/main/resources/ParquetFiles/YelpReviews")
    val convertToTupleArray = (s: String) => removeStopWords(s.split("\\s+"))



    sqlContext.udf.register("convert", convertToTupleArray)
    reviewData.registerTempTable("reviewData")
    val scrubbedData = sqlContext.sql("Select *, convert(content) as words from reviewData")
    val trainingData = scrubbedData.randomSplit(Array(.2,.8))(0)

    val word2Vec = new Word2Vec().setInputCol("words").setOutputCol("vector")
    val model = word2Vec.fit(trainingData)
    model.save("WordVecModel")
    //    val model = Word2VecModel.load("WordVecModel")
//   val dataWithVector = model.transform(scrubbedData)
//   dataWithVector.show()
  }

  def loadStopWords(): HashSet[String] = {
    val set = new HashSet[String]
    val fileLocation = "/Users/itawfik/IdeaProjects/YelpModel/src/main/resources/stop-word-list.csv"
    Source.fromFile(fileLocation).getLines().foreach(s => s.split(",").foreach(item => set.add(item.trim)))
    return set
  }

  def removeStopWords(input:Array[String]): Array[String] ={

    var resultArray:Array[String] = Array()
    for(word <- input){
      val lowerCaseAndtrimmed = word.toLowerCase.replaceAll("[^A-Za-z]","").trim
      if(!stopWords.contains(lowerCaseAndtrimmed) && lowerCaseAndtrimmed != ""){
        resultArray = resultArray :+ lowerCaseAndtrimmed
      }
    }
    resultArray
  }


}
