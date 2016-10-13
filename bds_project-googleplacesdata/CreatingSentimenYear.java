import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author sarthak jain
 *
 */
//File to process Yelp Review Data and convert it to a 
// YelpID, Year, AllReviewText for that restaurant in that year
public class CreatingSentimenYear {

	public static void main(String[] args) throws IOException {

		// HashSet<String> uniqueIdYear = new HashSet<String>();
		HashMap<String, String> idYearContentMapping = new HashMap<String, String>();
		ArrayList<String> fileNames = new ArrayList<String>();
		// HashSet<String> uniqueRestaurants = new HashSet<String>();
		fileNames.add("Review1.csv.xls");
		 fileNames.add("Review2.csv.xls");
		 fileNames.add("Review3.csv.xls");
		 fileNames.add("Review4.csv.xls");
		 fileNames.add("Review5.csv.xls");
		 fileNames.add("Review6.csv.xls");
		 fileNames.add("Review7.csv.xls");
		 fileNames.add("Review8.csv.xls");
		 fileNames.add("Review9.csv.xls");
		 fileNames.add("Review10.csv.xls");
		 fileNames.add("Review11.csv.xls");
		 fileNames.add("Review12.csv.xls");
		 fileNames.add("Review13.csv.xls");
		 fileNames.add("Review14.csv.xls");
		fileNames.add("Review15.csv.xls");
		fileNames.add("Review16.csv.xls");
		fileNames.add("Review17.csv.xls");
		fileNames.add("Review18.csv.xls");
		fileNames.add("Review19.csv.xls");
		fileNames.add("Review20.csv.xls");
		fileNames.add("Review21.csv.xls");
//		 fileNames.add("Review-extra3.csv.xls");
//		 fileNames.add("Review-extra12.csv.xls");

		FileWriter writer = new FileWriter(new File("FinalTopicModelData.csv"));

		for (String tempFile : fileNames) {

			int count = 0;
			BufferedReader reader1 = new BufferedReader(new FileReader(
					new File(tempFile)));
			String line = "";
			while ((line = reader1.readLine()) != null) {
				count++;
				try {
					String[] splitted = line
							.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
					String tempYelpId = splitted[0];
					// System.out.println(tempFile+"\t"+count);
					String year = "";
					if (splitted[5].contains("-")) {
						String[] tempDateSplit = splitted[5].split("-");
						year = tempDateSplit[0];
					} else if (splitted[5].contains("/")) {
						String[] tempDateSplit = splitted[5].split("/");
						year = tempDateSplit[2];
					} else {
						continue;
					}

					String tempContent = splitted[6].replaceAll(
							"[^a-zA-Z0-9 ]", "");
					tempContent = tempContent.replace("\n", "");
					tempContent = tempContent.replace("\r", "");

					tempContent = tempContent.replace("\"", "");
					tempContent = tempContent.replace(".", "");
					tempContent = tempContent.replace(")", "");
					tempContent = tempContent.replace("(", "");

					if (!idYearContentMapping.containsKey(tempYelpId + ","
							+ year)) {
						idYearContentMapping.put(tempYelpId + "," + year,
								tempContent);

					} else if (idYearContentMapping.containsKey(tempYelpId
							+ "," + year)) {

						idYearContentMapping.put(
								tempYelpId + "," + year,
								idYearContentMapping.get(tempYelpId + ","
										+ year)
										+ " " + tempContent);
					}
				} catch (Exception intentionallyIgnored) {
					System.out.println(intentionallyIgnored);
					continue;
				}

			}
			reader1.close();
			System.out.println(idYearContentMapping.size());
		}

		for (String key : idYearContentMapping.keySet()) {
			writer.write(key + "," + idYearContentMapping.get(key));
			writer.write("\n");
		}
		writer.flush();
		writer.close();

	}
}
