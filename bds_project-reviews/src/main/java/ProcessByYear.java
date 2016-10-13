package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.codehaus.jackson.map.ObjectMapper;

public class ProcessByYear {
	
	public void go(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("RestaurantsWithMinMaxDates/RestaurantsWithMinMaxDates.csv"));
		Map<String, Map<String, Restaurant>> map = new HashMap<String, Map<String, Restaurant>>();
		String line;
		while ((line = br.readLine()) != null) {
			String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			processRow(row, map);
		}

		br.close();
//		PrintWriter writer = new PrintWriter("RegionCounts", "UTF-8");
//		
//		for (String puma : map.keySet()) {
//			writer.println("Region: " + puma + " ----- Number of restaurants: " + map.get(puma).keySet().size());
//		}
//		writer.close();
		
		// Puma --> Year --> Restaurants Opened
		Map<String, Map<Integer, List<String>>> pumaToYearToRestaurantOpenedId = new HashMap<String, Map<Integer, List<String>>>();
		for (Entry<String, Map<String, Restaurant>> entry : map.entrySet()) {
			String puma = entry.getKey();
			Map<String, Restaurant> restaurantMap = entry.getValue();
			Map<Integer, List<String>> yearToRestaurant = new HashMap<Integer, List<String>>();
			for (Entry<String, Restaurant> restaurantEntry : restaurantMap.entrySet()) {
				String resId = restaurantEntry.getKey();
				Restaurant res = restaurantEntry.getValue();
				int yearOpened = res.minDate;
				if (yearOpened == Restaurant.MIN_DATE_FALSE)
					continue;
				List<String> restaurantsInYear = yearToRestaurant.get(yearOpened);
				if (restaurantsInYear == null) {
					yearToRestaurant.put(yearOpened, new ArrayList<String>(Arrays.asList(resId)));
				} else {
					restaurantsInYear.add(resId);
				}
			}
			pumaToYearToRestaurantOpenedId.put(puma, yearToRestaurant);
		}
		
		PrintWriter writer = new PrintWriter("PumaToYearToRestaurantId.csv", "UTF-8");
		writer.println("Region, YearOpened, Restaurant");
		for (Entry<String, Map<Integer, List<String>>> entry : pumaToYearToRestaurantOpenedId.entrySet()) {
			String puma = entry.getKey();
			Map<Integer, List<String>> yearToRestaurantList = entry.getValue();
			for (Entry<Integer, List<String>> yearToRestaurant : yearToRestaurantList.entrySet()) {
				Integer year = yearToRestaurant.getKey();
				List<String> restaurants = yearToRestaurant.getValue();
				for (String restaurant : restaurants) {
					writer.println(String.format("%s,%s,%s",puma, year, restaurant));
				}
			}
		}
		writer.close();
		
//		Map<String, Map<Integer, Integer>> pumaToYearToRestaurantOpenedId2 = new HashMap<String, Map<Integer, Integer>>();
//		for (Entry<String, Map<String, Restaurant>> entry : map.entrySet()) {
//			String puma = entry.getKey();
//			Map<String, Restaurant> restaurantMap = entry.getValue();
//			Map<Integer, Integer> yearToRestaurant = new HashMap<Integer, Integer>();
//			for (Entry<String, Restaurant> restaurantEntry : restaurantMap.entrySet()) {
//				Restaurant res = restaurantEntry.getValue();
//				int yearOpened = res.minDate;
//				if (yearOpened == Restaurant.MIN_DATE_FALSE)
//					continue;
//				Integer restaurantsInYear = yearToRestaurant.get(yearOpened);
//				if (restaurantsInYear == null) {
//					yearToRestaurant.put(yearOpened, 1);
//				} else {
//					yearToRestaurant.put(yearOpened, restaurantsInYear+1);;
//				}
//			}
//			pumaToYearToRestaurantOpenedId2.put(puma, yearToRestaurant);
//		}
//		
//		for (String puma : map.keySet()) {
//			writer.println("Region: " + puma + " ----- Number of restaurants: " + map.get(puma).keySet().size());
//		}
//		writer.close();
//		PrintWriter writer = new PrintWriter("PumaToYearToNumOfRestaurantsOpened.csv", "UTF-8");
//		writer.println("Puma,Year,NumOfRestaurantsOpened");
//		for (Entry<String, Map<Integer, Integer>> entry : pumaToYearToRestaurantOpenedId2.entrySet()) {
//			Map<Integer, Integer> entryMap = entry.getValue();
//			String puma = entry.getKey();
//			for (Entry<Integer, Integer> mapEntry : entryMap.entrySet()) {
//				writer.println(String.format("%s,%d,%d", puma, mapEntry.getKey(), mapEntry.getValue()));
//			}
//		}
//		writer.close();
		
//		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("RegionToYearToNumRestaurants"), pumaToYearToRestaurantOpenedId2);
	}

	private void processRow(String[] row, Map<String, Map<String, Restaurant>> map) {
		String restaurantId = row[0];
		String puma = row[7];
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(puma);
		if (!matcher.matches())
			return;
		Map<String, Restaurant> restaurantsInPuma = map.get(puma);
		Row resRow = null;
		if (row.length == 14) {
			resRow = new Row(row[1], row[2], row[3], row[4], row[5], row[6], row[8], row[9], row[10], row[11], row[12], row[13], row[14], row[15]);
		} else {
			resRow = new Row(row[1], row[2], row[3], row[4], row[5], row[6], row[8], row[9], row[10], row[11], row[12], row[13], row[14], row[15]);
		}
		
		if (restaurantsInPuma != null) {
			//puma present
			Restaurant restaurant = restaurantsInPuma.get(restaurantId);
			if (restaurant != null) {
				//restaurant present
				restaurant.addRow(resRow);
			} else {
				Restaurant res = new Restaurant(restaurantId, new ArrayList<Row>());
				res.addRow(resRow);
				restaurantsInPuma.put(res.id, res);
			}
		} else {
			//puma not present
			Map<String, Restaurant> pumaRestaurants = new HashMap<String, Restaurant>();
			Restaurant res = new Restaurant(restaurantId, new ArrayList<Row>());
			res.addRow(resRow);
			pumaRestaurants.put(res.id, res);
			map.put(puma, pumaRestaurants);
		}
		
	}
	
	public static void main(String[] args) {
		try {
			new ProcessByYear().go(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
