import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


// checking if all the Yelp Review files are unique
/**
 * 
 * @author sarthak jain
 *
 */
public class CheckingUniqueRestaurants {

	public static void main(String args[]) throws IOException {
		HashMap<String, String> uniqueYelpIds = new HashMap<String, String>();
		HashSet<String> unique = new HashSet<String>();
		ArrayList<String> fileNames = new ArrayList<String>();

		fileNames.add("Review/Review1.csv.xls");
		fileNames.add("Review/Review2.csv.xls");
		fileNames.add("Review/Review3.csv.xls");
		fileNames.add("Review/Review4.csv.xls");
		fileNames.add("Review/Review5.csv.xls");
		fileNames.add("Review/Review6.csv.xls");
		fileNames.add("Review/Review7.csv.xls");
		fileNames.add("Review/Review8.csv.xls");
		fileNames.add("Review/Review9.csv.xls");
		fileNames.add("Review/Review10.csv.xls");
		fileNames.add("Review/Review11.csv.xls");
		fileNames.add("Review/Review12.csv.xls");
		fileNames.add("Review/Review13.csv.xls");
		fileNames.add("Review/Review14.csv.xls");
		fileNames.add("Review/Review15.csv.xls");
		fileNames.add("Review/Review16.csv.xls");
		fileNames.add("Review/Review17.csv.xls");
		fileNames.add("Review/Review18.csv.xls");
		fileNames.add("Review/Review19.csv.xls");
		fileNames.add("Review/Review20.csv.xls");
		fileNames.add("Review/Review21.csv.xls");

		for (String tempFile : fileNames) {
			BufferedReader reader1 = new BufferedReader(new FileReader(
					new File(tempFile)));
			String line = "";
			while ((line = reader1.readLine()) != null) {
				String[] fields = line.split(",");
				uniqueYelpIds.put(fields[0], "");
				unique.add(fields[0]);
			}
		}
		System.out.println(uniqueYelpIds.size());
		System.out.println(unique.size());
		Iterator it = unique.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}

	}
}
