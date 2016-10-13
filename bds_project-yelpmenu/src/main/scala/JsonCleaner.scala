import java.util.regex.{Matcher, Pattern}

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
 * Created by islam on 3/28/16.
 */
object JsonCleaner {
  def main(args: Array[String]) {
    println(readJson("places.json"))
  }


  //Formats the json then reads it as an array
  def readJson (filePath: String): String ={
    implicit val formats = DefaultFormats
    val file = scala.io.Source.fromFile(filePath)
    val unformattedJson = try file.mkString finally file.close()
    unformattedJson.replaceAll("[\\s\\t\\n] && R^[\"*\"]", "")
  }

}
