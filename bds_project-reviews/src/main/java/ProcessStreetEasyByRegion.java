package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

public class ProcessStreetEasyByRegion {
	public void go(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("StreetEasyWithRegions.csv/streetEasyWithRegions.csv"));
		Map<String, Set<Listing>> map = new HashMap<String, Set<Listing>>();
		String line;
		while ((line = br.readLine()) != null) {
			String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			processRow(row, map);
		}
		br.close();
		
//		PrintWriter writer = new PrintWriter("RegionCountsListings", "UTF-8");
//
//		for (String puma : map.keySet()) {
//			writer.println("Region: " + puma + " ----- Number of listings: " + map.get(puma).size());
//		}
//		writer.close();
//
//		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("ListingsByRegion"), map);
		
		PrintWriter writer = new PrintWriter("RegionToIdBedroomsPrice.csv", "UTF-8");
		writer.println("Region,ListingID,Bedroms,Price");
		for (Entry<String, Set<Listing>> entry : map.entrySet()) {
			String puma = entry.getKey();
			Set<Listing> listings = entry.getValue();
			for (Listing listing : listings) {
				writer.println(String.format("%s,%s,%s,%s", puma, listing.id, listing.bedrooms, listing.price));
			}
		}
		writer.close();
	}
	
	private void processRow(String[] row, Map<String, Set<Listing>> map) {
		String puma = row[3];
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(puma);
		if (!matcher.matches())
			return;
		Set<Listing> listings = map.get(puma);
		Listing listing = new Listing(row[0], row[1], row[2]);
		if (listings != null) {
			listings.add(listing);
		} else {
			Set<Listing> set =  new HashSet<Listing>();
			set.add(listing);
			map.put(puma, set);
		}
	}
	
	public static void main(String[] args) {
		try {
			new ProcessStreetEasyByRegion().go(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
