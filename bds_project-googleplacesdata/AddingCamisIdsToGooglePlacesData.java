import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author sarthak jain
 *
 */

// transferring Camis ids form unique_Restaurants.txt to google places data
public class CountGPObjects {
	public static void main(String args[]) throws JSONException, IOException {

		ArrayList<String> idsFromUnique = new ArrayList<String>();

		FileWriter writer = new FileWriter(new File("GooglePlacesDataWithCamisIds"));
		BufferedReader reader1 = new BufferedReader(
				new FileReader(
						new File(
								"C:/Users/sj182/Desktop/bds project current/18April/jsonWithCamisIdsProperOrder.txt")));
		String line1 = "";

		while ((line1 = reader1.readLine()) != null) {

			JSONObject restaurant1 = new JSONObject(line1);
			String id = (String) restaurant1.get("id");
			idsFromUnique.add(id);

		}
		BufferedReader readerGP = new BufferedReader(
				new FileReader(
						new File(
								"C:/Users/sj182/Desktop/bds project current/18April/PlacesDataLineByLine.json")));
		String lineGP = "";
		int i = 0;
		JSONArray GP = new JSONArray();
		while ((lineGP = readerGP.readLine()) != null) {

			JSONObject restaurantGP = new JSONObject(lineGP);
			restaurantGP.put("CAMISID", idsFromUnique.get(i));
			i++;
			GP.put(restaurantGP);
		}

		for (int j = 0; j < GP.length(); j++) {

			writer.write(GP.getJSONObject(j).toString());
			writer.write("\n");

		}
		writer.flush();
		writer.close();

	}
}
