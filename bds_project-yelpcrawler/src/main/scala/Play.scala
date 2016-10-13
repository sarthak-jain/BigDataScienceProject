/**
 * Created by itawfik on 3/13/16.
 */

import org.json4s._
import org.json4s.jackson.JsonMethods._
object Play {



  def main(args: Array[String]) {
    val rest = NYCRest("HOT BAGELS", "MANHATTEN","West End", "110", "10023")
    val DBA = rest.DBA.replace(" ", "+")
    val BORO = rest.BORO.replace(" ", "+")
    val STREET = rest.STREET.replace(" ", "+")
    val BUILDING = rest.BUILDING.replace(" ", "+")
    val ZIPCODE = rest.ZIPCODE.replace(" ", "+")
    val test = s"http://www.yelp.com/search?find_desc=$DBA&find_loc=$BUILDING+$STREET,+$BORO,+NY+$ZIPCODE"
    val  emoty = "";
  }
}
