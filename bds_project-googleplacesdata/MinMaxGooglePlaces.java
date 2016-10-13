import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
//import java.text.ParseException;
import java.text.SimpleDateFormat;

// Calculating the minimum and Maximum dates for a restaurant based on google places review
/**
 * 
 * @author sarthak jain
 *
 */
public class MinMaxGooglePlaces {

	public static void main(String args[]) throws FileNotFoundException,
			IOException, ParseException, java.text.ParseException {
		// getting min max dates out of google places review data
		int countReview = 0;
		String idOfRest = "";
		Calendar c = Calendar.getInstance();
		ArrayList<String> tempListOfDates = new ArrayList<String>();
		HashMap<String, String> monthToNumberFormatStore = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> idDateMap = new HashMap<String, ArrayList<String>>();
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				"RestaurantMinMaxDates.txt"));
		writer.write("Restaurant id" + "\t" + "max" + "\t" + "min");
		writer.write("\n");
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(new FileReader(
				"PlacesDataProperJson.json"));

		monthToNumberFormatStore.put("Jan", "01");
		monthToNumberFormatStore.put("Feb", "02");
		monthToNumberFormatStore.put("Mar", "03");
		monthToNumberFormatStore.put("Apr", "04");
		monthToNumberFormatStore.put("May", "05");
		monthToNumberFormatStore.put("Jun", "06");
		monthToNumberFormatStore.put("Jul", "07");
		monthToNumberFormatStore.put("Aug", "08");
		monthToNumberFormatStore.put("Sep", "09");
		monthToNumberFormatStore.put("Oct", "10");
		monthToNumberFormatStore.put("Nov", "11");
		monthToNumberFormatStore.put("Dec", "12");

		for (Object o : a) {
			idOfRest = "";
			tempListOfDates.clear();
			JSONObject restaurant = (JSONObject) o;
			if (restaurant.containsKey("id")) {
				idOfRest = (String) restaurant.get("id");

			} else {
				continue;
			}
			if (restaurant.containsKey("reviews")) {
				JSONArray reviews = (JSONArray) restaurant.get("reviews");
				if (reviews.size() == 0 || reviews == null) {
					continue;
				}
				for (Object reviewIter : reviews) {
					JSONObject reviewObject = (JSONObject) reviewIter;
					if (reviewObject.containsKey("time")) {
						String time = (String) reviewObject.get("time");
						String[] timeBreakDown = time.split("\\W");
						time = timeBreakDown[7]
								+ "-"
								+ monthToNumberFormatStore
										.get(timeBreakDown[1]) + "-"
								+ timeBreakDown[2];
						tempListOfDates.add(time);
						countReview++;
					}
				}
				idDateMap.put(idOfRest, tempListOfDates);
			} else {
				continue;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<String> tempTimeListIs = new ArrayList<String>();
		// try {
		for (String idOfRestaurant : idDateMap.keySet()) {
			tempTimeListIs = idDateMap.get(idOfRestaurant);

			// max calculation
			Date date1 = sdf.parse("1980-01-01");
			System.out.println(tempTimeListIs.size());
			for (int p = 0; p < tempTimeListIs.size(); p++) {
				Date date2 = new Date(tempTimeListIs.get(p));
				System.out.println(date2);
				if (date2.after(date1)) {
					date1 = date2;
				}
			}

			// min calculation
			Date date3 = sdf.parse("2016-11-01");
			for (int p = 0; p < tempTimeListIs.size(); p++) {
				Date date4 = new Date(tempTimeListIs.get(p));
				if (date4.before(date3)) {
					date3 = date4;
				}
			}

			c.setTime(date1);
			String year1 = Integer.toString(c.get(Calendar.YEAR));
			String month1 = Integer.toString(c.get(Calendar.MONTH));
			String day1 = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
			c.setTime(date3);
			String year3 = Integer.toString(c.get(Calendar.YEAR));
			String month3 = Integer.toString(c.get(Calendar.MONTH));
			String day3 = Integer.toString(c.get(Calendar.DAY_OF_MONTH));

			// written in mm/dd/yyyy format

			String tempDate1Is = month1 + "/" + day1 + "/" + year1;
			String tempDate3Is = month3 + "/" + day3 + "/" + year3;
			writer.write(idOfRestaurant + "\t" + tempDate1Is + "\t"
					+ tempDate3Is);
			writer.write("\n");

		}
		// } catch (ParseException ex) {
		// ex.printStackTrace();
		// }

		writer.flush();
		writer.close();

		// System.out.println(countReview);
		// System.out.println(idDateMap.size());
	}
}
