import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import org.jsoup.nodes.{Element}
import purecsv.unsafe._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by itawfik on 3/22/16.
  */
class LinkIterator(val fileLocation:String, val startingIndex:Int) extends Iterator[String]{

  var currentIndex = 0
  //Split the json at the starting index and take the second set, this is here in case the process fails we can recover
  //where we left off
  val restList: Array[NYCRest] = readJson(fileLocation).splitAt(startingIndex)._2
  val s = ""

  override def hasNext: Boolean = {
    restList.size > currentIndex
  }

  override def next(): String = {
    println(currentIndex + startingIndex)
    val candidates = findResturaunts("http://www.yelp.com", createSearchString(restList(currentIndex)))
    println(restList(currentIndex))
    currentIndex = currentIndex + 1
    if(candidates.isEmpty){
      return ""
    }else{
      return candidates(0)
    }
  }

  //Formats the json then reads it as an array
  def readJson (filePath: String): Array[NYCRest] ={
    implicit val formats = DefaultFormats
    val file = scala.io.Source.fromFile(filePath)
    val unformattedJson = try file.mkString finally file.close()
    val commaSeperate = unformattedJson.replace("}", "},")
    val formattedJson = "[" + commaSeperate.dropRight(1) + "]"
    parse(formattedJson).extract[Array[NYCRest]]
  }

  def findResturaunts(yelp: String, searchString: String): Array[String] ={
    val browser = new Browser
    //val doc = browser.get(baseUrl + startIndex)
    try{
      val doc = browser.get(searchString)
      val links: List[Element] = doc >> elementList(".biz-Name");
      val distance: List[Element] = doc >> elementList(".search-result_tags")
      val d2 = distance
        .map(e => e.text())
        .map(s => {
          try{
            s.replace(" Miles", "").toDouble
          }catch {
            case e: Exception=> 9999.99
          }
        })

      val linksWIthDist = links.zip(d2)
      val test = linksWIthDist.sortBy(_._2 )
      test.unzip._1
        .map(e => e.attr("href"))
        .filter(s => s!=null && s.startsWith("/biz/"))
        .map(s => yelp + s)
        .toArray
    }catch{
      case e: Exception => return  Array("")
    }
  }

  def createSearchString(rest: NYCRest): String ={
    val DBA = rest.DBA.replace(" ", "+")
    val BORO = rest.BORO.replace(" ", "+")
    val STREET = rest.STREET.replace(" ", "+")
    val BUILDING = rest.BUILDING.replace(" ", "+")
    val ZIPCODE = rest.ZIPCODE.replace(" ", "+")
    s"http://www.yelp.com/search?find_desc=$DBA&find_loc=$BUILDING+$STREET,+$BORO,+NY+$ZIPCODE"
  }
}