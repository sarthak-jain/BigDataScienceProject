package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java .io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class MergeYelpReviews {
	public void go()  throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("YelpReviewsContentByYear.csv/YelpReviewscontentByYear.csv"));
		br.readLine();
		String line;
		Map<resAndYear, StringBuilder> map = new HashMap<resAndYear, StringBuilder>();
		Pattern pattern = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		while ((line = br.readLine()) != null) {
			if (line.contains("null"))
				continue;
			String[] row = pattern.split(line);
			if (row.length < 3)
				continue;
			String review = row[2];
			resAndYear resAndYear = new resAndYear(row[0], row[1]);
			StringBuilder content = map.get(resAndYear);
			if (content != null) {
				content.append(" " + review);
			} else {
				map.put(resAndYear, new StringBuilder(review));
			}
		}
		br.close();
		System.out.println("ready");
		BufferedWriter bw = new BufferedWriter(new FileWriter("YelpEverythingReviews.csv"));
		bw.write("Restaurant,Year,Content\n");
		for (Entry<resAndYear, StringBuilder> entry : map.entrySet()) {
			bw.write(String.format("%s,%s,%s\n", entry.getKey().Restaurant, entry.getKey().Year, entry.getValue().toString()));
		}
		bw.close();
	}
	
	public static void main(String[] args) {
		try {
			new MergeYelpReviews().go();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AllArgsConstructor
	@EqualsAndHashCode
	private class resAndYear {
		public String Restaurant;
		public String Year;
	}

}
