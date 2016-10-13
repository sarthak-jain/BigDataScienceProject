import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

// This class is to convert the google places data to the required format for topic model computation
/**
 * 
 * @author sarthak jain
 *
 */
public class GooglePlacesConversionToTopicModelData {

	public static void main(String args[]) throws IOException {

		// writing for the restaurant file

		// =>Formatted address
		// =>rating
		// =>user_ratings_total
		// =>price_level
		// =>name
		// =>id
		// =>place_id
		FileWriter writerR = new FileWriter(new File(
				"TopicModelRestaurantFile.csv"));
		writerR.write("formatted_address,rating,user_ratings_total,price_level,name,id,place_id");
		writerR.write("\n");
		// writing for the review file
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"PlacesDataLineByLine.json")));
		String line = "";
		FileWriter writer = new FileWriter(new File("TopicModelReviewFile.csv"));
		writer.write("id,place_id,author_name,language,text,time");
		writer.write("\n");
		int count = 1;
		while ((line = reader.readLine()) != null) {
			count++;
			JSONObject restaurantObj = new JSONObject(line);
			JSONArray arrayOfReviews = new JSONArray();
			String id = "";
			String placeId = "";

			if (restaurantObj.has("id")) {
				id = restaurantObj.getString("id");

			}
			if (restaurantObj.has("place_id")) {
				placeId = restaurantObj.getString("place_id");
			}
			String formatted_address = "";
			String rating = "null";
			String user_ratings_total = "null";
			String price_level = "null";
			String name = "";

			if (restaurantObj.has("formatted_address")) {
				formatted_address = restaurantObj
						.getString("formatted_address");

			}
			if (restaurantObj.has("rating")) {
				rating = "" + restaurantObj.getInt("rating");
			}
			if (restaurantObj.has("user_ratings_total")) {
				user_ratings_total = ""
						+ restaurantObj.getInt("user_ratings_total");

			}
			if (restaurantObj.has("price_level")) {
				price_level = "" + restaurantObj.getInt("price_level");
			}
			if (restaurantObj.has("name")) {
				name = restaurantObj.getString("name");

			}
			System.out.println(rating);
			writerR.write('"' + formatted_address.replaceAll(",", " ") + '"');
			writerR.write(",");
			writerR.write(rating.replaceAll(",", " "));
			writerR.write(",");
			writerR.write(user_ratings_total.replaceAll(",", " "));
			writerR.write(",");
			writerR.write(price_level.replaceAll(",", " "));
			writerR.write(",");
			writerR.write('"' + name.replaceAll(",", " ") + '"');
			writerR.write(",");
			writerR.write(id.replaceAll(",", " "));
			writerR.write(",");
			writerR.write(placeId.replaceAll(",", " "));
			writerR.write("\n");

			String author_name = "";
			String language = "";
			String text = "";
			String time = "";
			if (restaurantObj.has("reviews")) {
				arrayOfReviews = restaurantObj.getJSONArray("reviews");
			} else {
				continue;
			}
			for (int i = 0; i < arrayOfReviews.length(); i++) {

				JSONObject reviewObj = arrayOfReviews.getJSONObject(i);
				if (reviewObj.has("author_name")) {
					author_name = reviewObj.getString("author_name");
				}
				if (reviewObj.has("language")) {
					language = reviewObj.getString("language");
				}
				if (reviewObj.has("text")) {
					text = reviewObj.getString("text");
				}
				if (reviewObj.has("time")) {
					time = reviewObj.getString("time");
				}

				writer.write(id.replaceAll(",", " "));
				writer.write(",");
				writer.write(placeId.replaceAll(",", " "));
				writer.write(",");
				writer.write(author_name.replaceAll(",", " "));
				writer.write(",");
				writer.write(language.replaceAll(",", " "));
				writer.write(",");
				text = text.replaceAll("\n", " ");
				text = text.replaceAll("\r", "");
				text = text.replaceAll(",", " ");
				writer.write('"' + text + '"');
				writer.write(",");
				writer.write(time.replaceAll(",", " "));
				writer.write("\n");
				// **************remove spaces and lines from text
			}
			// fields to put in review file

			// =>id
			// =>place_id
			// =>author_name
			// =>author_url
			// =>aspects: rating, type
			// =>rating
			// =>language
			// =>text
			// =>time

		}
		writer.flush();
		writer.close();
		writerR.flush();
		writerR.close();
	}
}
