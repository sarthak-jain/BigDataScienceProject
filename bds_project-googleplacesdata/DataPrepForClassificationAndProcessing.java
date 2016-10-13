import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

// data preparation for clustering and Classification using RapidMiner
/**
 * 
 * @author sarthak jain
 *
 */
public class DataPrepForClassificationAndProcessing {

	public static void main(String args[]) throws IOException {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					"GooglePlacesData_OnlyNewYork.txt")));
			String line = "";
			FileWriter writer = new FileWriter("ForClustering.txt");
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count = count + 1;

				JSONObject objUnique = new JSONObject(line);
				if (objUnique.has("types") && objUnique.has("rating")) {
					JSONArray types = objUnique.getJSONArray("types");
					String firstType = types.getString(0).trim().toString();
					Double rating = Double.parseDouble(objUnique.get("rating")
							.toString());
					String priceLevel = "unknown";
					if (rating >= 0 && rating < 1) {
						priceLevel = "veryLow";
					} else if (rating >= 1 && rating < 2) {
						priceLevel = "Low";

					} else if (rating >= 2 && rating < 3) {
						priceLevel = "moderate";

					} else if (rating >= 3 && rating < 4) {
						priceLevel = "high";

					} else if (rating >= 4 && rating <= 5) {
						priceLevel = "veryHigh";

					}
					writer.write(firstType + "," + priceLevel);
					writer.write("\n");
					System.out.println(firstType);

					System.out.println();

					System.out.println(priceLevel);
				} else {

					continue;
				}
			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
