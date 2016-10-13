import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import org.jsoup.nodes.{Element}
import purecsv.unsafe._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer

/**
 * Created by islam on 4/3/16.
 */
object IDCrawler {

  //Get the next element from the iterator

  //Find the Id

  //If iterator returns a value then create the extended version and save to csv, put 100 restaruatns to a fule
  def main(args: Array[String]) {
    val iter = new LinkIterator("unique_restaurants.txt", 0)
    val buffer = new ListBuffer[NYCRestExtended]
    var counter =0
    while(iter.hasNext){
      val link = iter.next()
      val establishment = getNameAndID(link)
      if(establishment._4){
        println("Found: " + counter)
        var currentRest = iter.getInput
        val yelpId = establishment._2
        val link = establishment._1
        val DBA = currentRest.DBA
        val BORO = currentRest.BORO
        val STREET = currentRest.STREET
        val BUILDING = currentRest.BUILDING
        val ZIPCODE = currentRest.ZIPCODE
        var rest = new NYCRestExtended(yelpId, link, DBA, BORO, STREET, BUILDING, ZIPCODE)
        buffer += rest
        counter = counter + 1
        if(counter % 20 == 0){
          buffer.writeCSVToFileName("IDMAPPING/" + counter + ".csv", "|")
          buffer.clear()
        }
      }
    }
  }

  def getNameAndID(link:String):(String,String, String, Boolean)={
    val browser = new Browser
    try{
      val doc =  browser.get(link)
      val name:String = doc >> text("h1")
      val id:String = doc.select("a[data-signup-object]").attr("data-signup-object").split(":")(1)
      (link,id,name, true)
    }catch{
      case e:Exception => ("","","",false)
    }
  }

}
