import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

/**
 * 
 * @author sarthak jain
 *
 */
// File for cleaning and merging the Yelp Data and Google places data  
public class JoiningYelpGP {

	public static void main(String[] args) throws IOException {

		// reading yelp

		BufferedReader reader1 = new BufferedReader(new FileReader(new File(
				"allYelpData")));

		String arr = "";
		String line = "";
		while ((line = reader1.readLine()) != null) {
			arr = arr + line;
		}
		reader1.close();
		JSONObject objYelp = new JSONObject(arr);

		// reading google places file
		BufferedReader readerGP = new BufferedReader(new FileReader(new File(
				"GooglePlacesOnlyNewYorkWithCamisIds.txt")));
		String lineGP = "";
		int countMatch = 0;
		int countMismatch = 0;
		while ((lineGP = readerGP.readLine()) != null) {
			JSONObject objGP = new JSONObject(lineGP);
			String camisGP = objGP.getString("CamisIds");
			if (camisGP.equals("none")) {
				continue;
			}

			objGP.remove("CamisIds");
			objGP.remove("icon");
			objGP.remove("address_components");
			objGP.remove("photos");
			objGP.remove("url");
			objGP.remove("formatted_phone_number");
			objGP.remove("international_phone_number");

			if (!objYelp.has(camisGP)) {
				JSONObject goog = new JSONObject();
				goog.put("google", objGP);
				objYelp.put(camisGP, goog);
			} else {
				boolean hasReview, hasYelp;
				JSONObject tempYelpObj = objYelp.getJSONObject(camisGP);
				hasYelp = tempYelpObj.has("yelp");
				objYelp.getJSONObject(camisGP).put("google", objGP);
				if ((hasYelp == objYelp.getJSONObject(camisGP).has("yelp"))) {
					countMatch++;
				} else {
					countMismatch++;
				}
			}
		}
		System.out.println("Match : " + countMatch);
		System.out.println("Mismatch : " + countMismatch);
		readerGP.close();
		FileWriter writer = new FileWriter(new File("allDataCombined"));
		writer.write(objYelp.toString(4));
		writer.flush();
		writer.close();
	}
}
