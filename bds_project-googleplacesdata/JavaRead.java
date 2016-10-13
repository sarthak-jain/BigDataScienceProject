import org.apache.spark.sql.SQLContext;
//import sqlContext.implicits._
import org.apache.spark.SparkContext;
import org.apache.spark.SparkConf;
import java.util.regex.*;
import org.json4s.DefaultFormats;
import org.json4s.jackson.JsonMethods.*;

/**
* @author- Sarthak Jain
*/
public class JavaRead{
public static void main(String args[]){

SparkConf conf = new SparkConf().setAppName(googlePlaces).setMaster(master);
SparkContext sc = new SparkContext(conf);

// sc is an existing JavaSparkContext.
SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

// A JSON dataset is pointed to by path.
// The path can be either a single text file or a directory storing text files.
DataFrame people = sqlContext.read().json("/home/cloudera/Documents/sarthak/outputPlaces.json");

// The inferred schema can be visualized using the printSchema() method.
people.printSchema();
// root
//  |-- age: integer (nullable = true)
//  |-- name: string (nullable = true)

// Register this DataFrame as a table.
people.registerTempTable("people");

// SQL statements can be run by using the sql methods provided by sqlContext.
DataFrame teenagers = sqlContext.sql("SELECT * from people");

// Alternatively, a DataFrame can be created for a JSON dataset represented by
// an RDD[String] storing one JSON object per string.
//List<String> jsonData = Arrays.asList(
  //"{\"name\":\"Yin\",\"address\":{\"city\":\"Columbus\",\"state\":\"Ohio\"}}");
//JavaRDD<String> anotherPeopleRDD = sc.parallelize(jsonData);
//DataFrame anotherPeople = sqlContext.read().json(anotherPeopleRDD);
}}
