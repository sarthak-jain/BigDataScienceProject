import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import org.jsoup.nodes.{Element}
import purecsv.unsafe._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer

object Crawler {
  case class menuItem(name:String, id:String, menuSection:String, item:String, price:String, reviewCount:String)


  def main(args: Array[String]) {

    val yelp = "http://www.yelp.com"
    val resturauntLookup = "unique_restaurants.txt"
    val resturauntList1 = resturauntList(resturauntLookup)
    val iterator = new LinkIterator(resturauntLookup,2194)
    var counter = 1051
    var buffer = new ListBuffer[Seq[menuItem]]

    while(iterator.hasNext) {
      var link = iterator.next()
      println(link)
      if(!link.equals("")){
        val validEstablishment = getNameAndID(link)
        //4th item in the tuple defines if there is a menu
        if(validEstablishment._4){
          val validMenu = menu(validEstablishment._1.replace("/biz/", "/menu/"), validEstablishment._3, validEstablishment._2)
          if(!validMenu.isEmpty){
            println("Found: " + counter)
            buffer += validMenu.get
            counter = counter + 1
            if(counter % 10 == 0){
              buffer.flatten.writeCSVToFileName("RawThird/" + counter + ".csv" , "|")
              buffer.clear()
            }
          }
        }
      }




    }
    //Write anything remaining in the buffer to file
    counter = counter + 1
    buffer.flatten.writeCSVToFileName("RawThird/" + counter + ".csv" , "|")
    buffer.clear()
  }

  def createSearchString(rest: NYCRest): String ={
    val DBA = rest.DBA.replace(" ", "+")
    val BORO = rest.BORO.replace(" ", "+")
    val STREET = rest.STREET.replace(" ", "+")
    val BUILDING = rest.BUILDING.replace(" ", "+")
    val ZIPCODE = rest.ZIPCODE.replace(" ", "+")
    s"http://www.yelp.com/search?find_desc=$DBA&find_loc=$BUILDING+$STREET,+$BORO,+NY+$ZIPCODE"
  }

  def resturauntList (filePath: String): List[NYCRest] ={
    implicit val formats = DefaultFormats
    val file = scala.io.Source.fromFile(filePath)
    val unformattedJson = try file.mkString finally file.close()
    val commaSeperate = unformattedJson.replace("}", "},")
    val formattedJson = "[" + commaSeperate.dropRight(1) + "]"
    parse(formattedJson).extract[List[NYCRest]]
  }

  def getNameAndID(link:String):(String,String, String, Boolean)={
    val browser = new Browser
    try{
      val doc =  browser.get(link)
      val name:String = doc >> text("h1")
      val id:String = doc.select("a[data-signup-object]").attr("data-signup-object").split(":")(1)
      val hasMenu = doc.select(".menu-explore").size() != 0
      (link,id,name, hasMenu)
    }catch{
      case e:Exception => ("","","",false)
    }

  }

  def findResturaunts(yelp: String, searchString: String): Seq[String] ={
    val browser = new Browser
    //val doc = browser.get(baseUrl + startIndex)
    try{
      val doc = browser.get(searchString)
      val links: List[Element] = doc >> elementList(".biz-Name");
      val distance: List[Element] = doc >> elementList(".search-result_tags")
      val d2 = distance
        .map(e => e.text())
        .map(s => s.replace(" Miles", "").toDouble)

      val linksWIthDist = links.zip(d2)
      val test = linksWIthDist.sortBy(_._2 )
      test.unzip._1
        .map(e => e.attr("href"))
        .filter(s => s!=null && s.startsWith("/biz/"))
        .map(s => yelp + s)
    }catch{
      case e: Exception => return  List("")
    }
  }

  def menu(url:String, resturantName:String, id:String): Option[Seq[menuItem]] ={
    val browser = new Browser

    try{
      val doc = browser.get(url)
      //for each section in the menu
      val section: List[Element] = doc >> elementList(".menu-section")
      val sectionHeaders: List[Element] = doc >> elementList(".menu-section-header")
      val aggregate = sectionHeaders zip section


      val ret = aggregate
        .map(e => e._2.children()
          .map(e2 => menuItem(removeQuotes(resturantName),
            removeQuotes(id),
            removeQuotes(e._1.text()),
            removeQuotes(e2.select(".menu-item-details").select("h4").text()),
            removeQuotes(e2.select(".menu-item-price-amount").text()),
            removeQuotes(e2.select(".menu-item-details").select(".menu-item-details-stats").text()))))

      Some(ret.flatten)
    }catch{
      case e: Exception => return None
    }
  }

  def removeQuotes(value:String):String ={
    value.replaceAll("\"","")
  }
}