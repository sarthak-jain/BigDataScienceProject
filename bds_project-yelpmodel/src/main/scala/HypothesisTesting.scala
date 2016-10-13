import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.ml.classification.{DecisionTreeClassifier, GBTClassifier, MultilayerPerceptronClassifier}
import org.apache.spark.ml.feature.{VectorIndexer, StringIndexer}
import org.apache.spark.ml.regression._
import org.apache.spark.mllib.linalg.{DenseVector, Vectors}
import org.apache.spark.mllib.regression.{LassoWithSGD, LinearRegressionWithSGD}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by itawfik on 5/7/16.
 */
object HypothesisTesting {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val conf = new SparkConf().setMaster("local[*]").setAppName("Census").set("spark.driver.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //Get the census data
    val census = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("inferSchema", "true")
      .option("delimiter", "|")
      .option("header","true")
      .load("/Users/itawfik/IdeaProjects/YelpModel/census/part-00000")
      .withColumnRenamed("avg(cleanedGRNTP)", "averageGrossRent")

    //Since this is a csv we have to convert the fields to the right format
    val convertToInt = (x:String) => x.toInt
    val convertToDouble = (x:String) => x.toDouble
    sqlContext.udf.register("toInt", convertToInt)
    sqlContext.udf.register("toDouble", convertToDouble)

    census.registerTempTable("census")

    val censusToNumbers = sqlContext.sql("Select toInt(year) as year," +
      " toInt(cleanedPuma) as puma, toInt(cleanedBDSP) " +
      "as bedrooms, toDouble(averageGrossRent) as grossRent from census")

    censusToNumbers.show()



    //Going to model only across one bedrooms
    val censusOnceBed = censusToNumbers.where("bedrooms = 1")

    val pchange = (firstYear:Double ,secondYear:Double) => (secondYear - firstYear)/firstYear
    sqlContext.udf.register("percentChange", pchange)

    //Join to its self to get the next years price, price increase and percentage change
    censusOnceBed.registerTempTable("census")
    censusOnceBed.registerTempTable("census2")
    val annualCensusChange = sqlContext.sql("Select census.* , census2.grossRent as nextYearRent, (census2.grossRent - census.grossRent) as annualChange," +
      "percentChange(census.grossRent,census2.grossRent) as percentChange from census left join census2 on " +
      "census.year + 1 = census2.year and census.puma = census2.puma and census.bedrooms = census2.bedrooms")
      .where("percentChange is not null")

    annualCensusChange.show()

    annualCensusChange.registerTempTable("census")
//    sqlContext.sql("Select annualChange from census").coalesce(1).write
//      .format("com.databricks.spark.csv")
//      .option("delimiter", "|")
//      .option("header", "true")
//      .save("annualChange.csv")


    //Get the data from review cluster, and join it to the census data
    val reviewClusters = sqlContext.read.parquet("clusteredByRegion")
    reviewClusters.registerTempTable("clusters1")
    reviewClusters.registerTempTable("clusters2")

    val temp = sqlContext.sql("Select clusters1.*, clusters2.allReviews as priorYearCount, clusters2.normalizedClusters as " +
      "priorYearNorm from clusters1 left join clusters2 " +
      "on clusters1.PUMACE10 = clusters2.PUMACE10 and clusters1.year - 1 = clusters2.year")
      .where("priorYearCount is not null")

    temp.show()


    //Doing an experiment where I merge the review vecotrs together to get a long vector that represents the change
    temp.registerTempTable("temp")

    val merge = (a:Seq[Double],b:Seq[Double]) => a ++ b
    val mergeInt = (a:Seq[Int],b:Seq[Int]) => a ++ b
    val totalReviewCount = (a:Seq[Int]) => a.reduce((x,y) => x + y)
    sqlContext.udf.register("merge", merge)
    sqlContext.udf.register("mergeInt", mergeInt)
    sqlContext.udf.register("reviewCount", totalReviewCount)



    sqlContext.sql("Select *,mergeInt(priorYearCount,allReviews) as mergedCounts , " +
      "merge(priorYearNorm,normalizedClusters) as mergedNorm from temp").where("reviewCount(allReviews) > 50")
      .registerTempTable("reviewClusters")

    sqlContext.sql("Select *,mergeInt(priorYearCount,allReviews) as mergedCounts , " +
      "merge(priorYearNorm,normalizedClusters) as mergedNorm from temp").where("reviewCount(allReviews) > 50").show()

    val convertToDoubleArray = (a:Seq[Int]) => Vectors.dense(a.toArray.map(i => i.toDouble))
    val convertToVector = (s:Seq[Double]) => Vectors.dense(s.toArray)
    sqlContext.udf.register("vectorize", convertToVector)
    sqlContext.udf.register("arrayToDoube", convertToDoubleArray)
//    reviewClusters.registerTempTable("reviewClusters")

    val finalDataSet = sqlContext.sql("Select *, arrayToDoube(allReviews) as reviewCountVector, arrayToDoube(mergedCounts) as vectorCounts " +
      ",vectorize(normalizedClusters) as reviewPercentVector, vectorize(mergedNorm) as vectorNorm   from " +
      "census left join reviewClusters on census.year = reviewClusters.year " +
      "and census.puma = reviewClusters.PUMACE10").where("PUMACE10 is not null")

    finalDataSet.show()

    finalDataSet.persist(StorageLevel.MEMORY_ONLY)

    val allWithClassification = appendClassification(finalDataSet, "percentChange","vectorNorm")

    allWithClassification.coalesce(1).write
          .format("com.databricks.spark.csv")
          .option("delimiter", "|")
          .option("header", "true")
          .save("classificationResults.csv")

    val allWithRegression = appendRegressionResults(finalDataSet, "percentChange","vectorNorm")
    allWithRegression.show()

  }


  def appendRegressionResults(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame ={
    var currentData = data
    currentData = appendRandomForest(currentData ,labelColumn, featureColumn)
    currentData = appendGradientBoostedTrees(currentData ,labelColumn, featureColumn)
    currentData = appendDecisonTrees(currentData ,labelColumn, featureColumn)
    currentData = appendLinearRegression(currentData ,labelColumn, featureColumn)
//    currentData = appendSurvivalRegression(currentData ,labelColumn, featureColumn)
    return currentData
  }

  def appendRandomForest(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame ={
        val randomForest = new RandomForestRegressor()
          .setLabelCol(labelColumn)
          .setFeaturesCol(featureColumn)
          .setPredictionCol("randomForestTreePrediction")

        //The data has been largely reduced in deimesions, but to aviod overfitting only train on part of the data
        val splitData = data.randomSplit(Array(.90,.10))
        val training = splitData(0)
        val randomForestModel = randomForest.fit(training)
        randomForestModel.transform(data)
  }

  def appendGradientBoostedTrees(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame ={
    val graidentBoostedTrees = new GBTRegressor()
      .setLabelCol(labelColumn)
      .setFeaturesCol(featureColumn)
      .setPredictionCol("GBTPrediction")

    //The data has been largely reduced in deimesions, but to aviod overfitting only train on part of the data
    val splitData = data.randomSplit(Array(.90,.10))
    val training = splitData(0)
    val gbt = graidentBoostedTrees.fit(training)
    gbt.transform(data)
  }

  def appendDecisonTrees(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame = {
        val dt = new DecisionTreeRegressor()
          .setLabelCol(labelColumn)
          .setFeaturesCol(featureColumn)
          .setPredictionCol("decisionTreePrediction")
        //The data has been largely reduced in deimesions, but to aviod overfitting only train on part of the data
        val splitData = data.randomSplit(Array(.90,.10))
        val training = splitData(0)

        val dtModel = dt.fit(training)
        dtModel.transform(data)
  }


  def appendLinearRegression(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame = {
        val linearRegression = new LinearRegression()
          .setLabelCol(labelColumn)
          .setFeaturesCol(featureColumn)
          .setPredictionCol("linearPrediction")

        //The data has been largely reduced in deimesions, but to aviod overfitting only train on part of the data
        val splitData = data.randomSplit(Array(.90,.10))
        val training = splitData(0)
        val linearModel = linearRegression.fit(training)
        linearModel.transform(data)
  }

  def appendClassification(data:DataFrame, labelColumn:String, featureColumn:String):DataFrame = {
    //Bin the data
    val binIt = (x:Double) => bin(x)
    data.registerTempTable("tempTable")
    data.sqlContext.udf.register("bin", binIt)
    var updatedData = data.sqlContext.sql("Select *, bin(annualChange) as labels from tempTable")


    updatedData = appendDecisionTreeClassifier(updatedData, "labels", featureColumn)
//    updatedData =  appendNueralNet(updatedData, labelColumn,featureColumn)
    updatedData
  }
  
  def appendNueralNet(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame ={
    val classifier = new MultilayerPerceptronClassifier()
      .setLabelCol("labels")
      .setFeaturesCol(featureColumn)
      .setPredictionCol("nueralNet")
      .setLayers(Array[Int](40, 5, 9, 7))


    val splitData = data.randomSplit(Array(.90,.10))
    val training = splitData(0)
    val classificationModel = classifier.fit(training)
    classificationModel.transform(data)
  }

  def appendDecisionTreeClassifier(data:DataFrame, labelColumn:String, featureColumn:String): DataFrame ={
    val stages = new ArrayBuffer[PipelineStage]()
    val labelIndexer = new StringIndexer().setInputCol(labelColumn).setOutputCol("indexedLabel").fit(data)
    val featureIndexer = new VectorIndexer().setInputCol(featureColumn).setOutputCol("indexedFeature").setMaxCategories(7).fit(data)
    val temp = featureIndexer.transform(labelIndexer.transform(data))

    val classifier = new DecisionTreeClassifier()
      .setLabelCol(labelIndexer.getOutputCol)
      .setFeaturesCol(featureIndexer.getOutputCol)
      .setPredictionCol("decisionTreeClassifier")

    val classificationModel = classifier.fit(temp)
    classificationModel.transform(temp)
  }

  def bin(x:Double): Double ={
    if(x < -200) return -3
    if(x < -100) return -2
    if(x < 0) return -1

    if(x > 200) return 3
    if(x > 100) return 2
    if(x > 0) return 1
    return 0
  }
}
