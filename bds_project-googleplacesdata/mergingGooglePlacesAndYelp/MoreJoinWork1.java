import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author sarthak jain
 *
 */
public class MoreJoinWork1 {
	public static void main(String args[]) throws IOException {
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(
				args[0])));
		String line = "";
		HashMap<String, JSONArray> reviewMap = new HashMap<String, JSONArray>();
		int count = 0;
		while ((line = reader1.readLine()) != null) {
			System.out.println(count++);
			JSONObject obj = new JSONObject(line);
			String yelpId = obj.getString("Restaurant");
			obj.remove("Restaurant");
			obj.remove("content");
			obj.remove("author");
			if (reviewMap.containsKey(yelpId)) {
				reviewMap.get(yelpId).put(obj);
			} else {
				reviewMap.put(yelpId, new JSONArray().put(obj));
			}
		}
		reader1.close();

		BufferedReader reader2 = new BufferedReader(new FileReader(new File(
				"mappingTableFinalJson.json")));
		HashMap<String, String> camisYelp = new HashMap<String, String>();
		while ((line = reader2.readLine()) != null) {
			JSONObject obj = new JSONObject(line);
			String yelpId = obj.get("YelpId").toString();

			if (!camisYelp.containsKey(yelpId)) {
				camisYelp.put(yelpId, obj.getString("CAMIS"));
			}
		}
		reader2.close();

		BufferedReader reader3 = new BufferedReader(new FileReader(new File(
				"yelpRestaurantJSON")));
		JSONObject finalObj = new JSONObject();
		while ((line = reader3.readLine()) != null) {
			JSONObject obj = new JSONObject(line);
			String yelpId = obj.getString("Restaurant");
			if (camisYelp.containsKey(yelpId)) {
				obj.remove("phone");
				obj.remove("yelpURL");
				JSONObject localObj = new JSONObject();
				localObj.put("yelp", obj);
				localObj.put("review", reviewMap.get(yelpId));
				finalObj.put(camisYelp.get(yelpId), localObj);
			}

		}
		reader3.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				"allYelpData"), true));
		writer.write(finalObj.toString());
		writer.close();
	}

}
