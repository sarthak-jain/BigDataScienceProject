import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;
import se.walkercrou.places.Param;
import se.walkercrou.places.Review;

/**
 * 
 * @author sarthak jain
 *
 */
// The below program is to rerieve restaurant details from the Google Places API
public class GooglePlacesApiPlaceDetails {

	public static void main(String[] args) throws InterruptedException,
			IOException {

		// reading the input file
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"unique_restaurants.txt")));
		String line = "";
		String str = "";
		while ((line = reader.readLine()) != null) {
			str += line;
		}
		FileWriter writer = new FileWriter(new File("PlacesData.json"));

		JSONArray inputFile = new JSONArray(str);
		JSONArray outputFile = new JSONArray();

		for (int j = 0; j < inputFile.length(); j++) {
			String query = "";
			JSONObject objTemp = inputFile.getJSONObject(j);
			GooglePlaces client = new GooglePlaces(
					"AIzaSyBX0_frvDWyPmQvmnZx4sUY2MMhKiiR7wo");
			query += objTemp.get("DBA") + ", " + objTemp.get("BUILDING") + " "
					+ objTemp.get("STREET") + ", " + objTemp.get("ZIPCODE");
			List<Place> places = null;
			try {
				// query for each restaurant
				places = client.getPlacesByQuery(query);
			} catch (Exception e) {
				writer.write("{}\n");
				continue;
			}
			if (places == null || places.size() == 0) {
				writer.write("{}\n");
				continue;
			}
			Place tempPlace;
			try {
				// get Details query for place retrieved by query
				tempPlace = places.get(0).getDetails();
			} catch (Exception e) {
				writer.write(places.get(0).getJson().toString());
				writer.write("\n");
				writer.flush();
				continue;
			}
			JSONObject tempJSONObjct = new JSONObject(tempPlace.getJson()
					.toString());
			try {
				// code to convert time for each review to a readable format
				JSONArray arr = new JSONArray(tempJSONObjct.get("reviews")
						.toString());
				for (int i = 0; i < arr.length(); i++) {
					try {
						Date date = new Date(
								Long.parseLong(arr.getJSONObject(i).get("time")
										.toString()) * 1000);
						arr.getJSONObject(i).put("time", date);
					} catch (Exception intentionallyIgnored) {
					}
				}
				tempJSONObjct.put("reviews", arr);
			} catch (Exception intentionallyIgnored) {
				writer.write(tempJSONObjct.toString());
				writer.write("\n");
				writer.flush();
				continue;
			}
			writer.write(tempJSONObjct.toString());
			writer.write("\n");
			writer.flush();
		}
		writer.flush();
		writer.close();
		reader.close();
	}
}
