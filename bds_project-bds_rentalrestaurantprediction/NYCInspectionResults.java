import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class NYCInspectionResults {
	private Map<String, Restaurant> idToRestaurantMap;
	private List<RestaurantFirstLastInspection> listOfEntries;
	
	public NYCInspectionResults() {
		this.idToRestaurantMap = new HashMap<String, Restaurant>();
		this.listOfEntries = new ArrayList<RestaurantFirstLastInspection>();
	}
	
	private void createMap(String inputFilename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFilename));
		String line;
		/*
		 * Columns:
		 * 1-id; 2-name, 3-boro, 4-building, 5-street, 6-zipcode, 7-phone, 8-cuisine,
		 * 9-inspectionDate, 10-action, 11-violationCode, 12-violationDescription, 13-criticalFlag, 14-score
		 * 15-grade, 16-gradeDate, 17-recordDate, 18-inspectionType
		 */
		//Column names
		line = br.readLine();
		while ((line = br.readLine()) != null) {
			//escape commas inside quotes
			String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String id = row[0];
			Restaurant restaurant;
			boolean flag = this.idToRestaurantMap.containsKey(id);
			if (flag) {
				restaurant = this.idToRestaurantMap.get(id);
			} else {
				restaurant = new Restaurant(id, row[1], row[2], row[3], row[4],row[5], row[6], row[7]);
			}
			if (row.length == 17) {
				Inspection inspection = new Inspection(row[8], row[9], row[10], row[11], row[12], row[13], row[14], row[15], row[16], "");
				restaurant.addInspection(inspection);
			} else {
				Inspection inspection = new Inspection(row[8], row[9], row[10], row[11], row[12], row[13], row[14], row[15], row[16], row[17]);
				restaurant.addInspection(inspection);
			}
			if (!flag) {
				this.idToRestaurantMap.put(restaurant.getId(), restaurant);
			}
			
		}
		br.close();
	}
	
	public void go(String inputFilename, String outputFilename) throws IOException {
		System.out.println("creating Map");
		createMap(inputFilename);
		this.listOfEntries = new ArrayList<RestaurantFirstLastInspection>(this.idToRestaurantMap.size());
		System.out.println("building list of entries");
		buildListOfEntries();
		System.out.println("writing to file");
		writeListOfEntriesToFile(outputFilename);
	}

	private void writeListOfEntriesToFile(String fileName) throws IOException {
		Writer writer = new BufferedWriter(new FileWriter(fileName));
		StringBuffer sb = new StringBuffer();
		for (RestaurantFirstLastInspection rfli : this.listOfEntries) {
//			System.out.println(rfli.toString());
			sb.append(rfli.toString() + '\n');
		}
		writer.write(sb.toString());
		writer.close();
	}

	private void buildListOfEntries() {
		for (Restaurant restaurant : this.idToRestaurantMap.values()) {
			try {
				RestaurantFirstLastInspection rfli = getFirstLastInspectionDates(restaurant);
//				System.out.println(rfli);
				this.listOfEntries.add(rfli);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private RestaurantFirstLastInspection getFirstLastInspectionDates(Restaurant restaurant) throws ParseException {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		List<Inspection> inspections = restaurant.getInspectionList();
		Date min = df.parse(inspections.get(0).getInspectionDate());
		Date max = df.parse(inspections.get(0).getInspectionDate());
		for (Inspection inspection : inspections) {
			Date inspectionDate = df.parse(inspection.getInspectionDate());
			if (inspectionDate.before(min)) {
				min = inspectionDate;
			}
			if (inspectionDate.after(max)) {
				max = inspectionDate;
			}
		}
		RestaurantFirstLastInspection rfli = new RestaurantFirstLastInspection(restaurant.getId(), df.format(min), df.format(max));
		return rfli;
	}
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Usage: NYCInspectionResults <inputFilename> <outputFilename>");
			System.exit(1);
		}
		try {
			new NYCInspectionResults().go(args[0], args[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
