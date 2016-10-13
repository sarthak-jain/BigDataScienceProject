import java.io.*;
import java.util.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class YelpCrawler {
	
	List<String> linksToVisit = new LinkedList<>();
	String entryLink = "https://www.yelp.com/search?find_desc=Restaurants&find_loc=New+York,+NY&start=";
	String baseLink = "https://www.yelp.com";
	String outputPath = ""; //"D:\\BDS_HW\\project\\yelpdata\\";
	String restaurantList = "unique_restaurants.txt";
	CSVFormat csvFileFormat = CSVFormat.DEFAULT;
	String restaurant = "Restaurant-extra";
	String review = "Review-extra";
	String errorLog = "errorLog-extra";
	int interval = 1000;
	PrintWriter pr = null;

	/**
	 * Usage: export jar file
	 * java -jar yelpCrawler.jar YelpCrawler [idx] [outputPath] [startIdx] [interval]
	 * default value of startIdx = (idx-1)*interval
	 * @param args
     */
	public static void main(String[] args){
		YelpCrawler c = new YelpCrawler();
		String idx = "";
		int startIdx = -1;
		if(args.length > 0){ idx = args[0]; }
		if(args.length > 1){ c.outputPath = args[1]; }
		if(args.length > 2){ startIdx = Integer.parseInt(args[2]); }
		if(args.length > 3){ c.interval = Integer.parseInt(args[3]); }
		String restaurantFile = c.outputPath+c.restaurant+idx+".csv";
		String reviewFile = c.outputPath+c.review+idx+".csv";
		LinkIterator itr = new LinkIterator(c.restaurantList, startIdx==-1?(Integer.parseInt(idx)-1)*c.interval: startIdx);

		CSVPrinter restaurantWriter = null;
		CSVPrinter reviewWriter = null;
		Document htmlDocument = null;
		try{
			restaurantWriter = new CSVPrinter(new FileWriter(restaurantFile), c.csvFileFormat);
			reviewWriter = new CSVPrinter(new FileWriter(reviewFile), c.csvFileFormat);
			c.pr = new PrintWriter(new File(c.outputPath+c.errorLog+idx+".txt"));
			while(itr.hasNext() && c.interval > 0){
				System.out.println("=====Restaurant left="+c.interval +"=====");
				c.interval--;
				String line = itr.next();
				if(!line.isEmpty()){
					htmlDocument = ConnectionUtil.getDocument(line);
					// If we didn't get the document, ignore this URL, write an error log and continue
					if(htmlDocument == null){ c.writeErrorLog(c.pr, line); continue; }
					Restaurant re = c.getRestaurant(htmlDocument);

					// Output yelp restaurant data to CSV
					c.writeCSVFile(restaurantWriter, re.getValueList());
					restaurantWriter.flush();

					List<Review> reviews = c.getReviews(htmlDocument, re);
					// Output yelp review data to CSV
					for(Review r: reviews){
						c.writeCSVFile(reviewWriter, r.getValueList());
					}
					reviewWriter.flush();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try{
				restaurantWriter.close();
				reviewWriter.close();
				c.pr.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	// parse yelp restaurant data
	public Restaurant getRestaurant(Document htmlDocument){
		try{
			Restaurant r = new Restaurant();
			Elements id = htmlDocument.select("a[data-signup-object]");
			Elements name = htmlDocument.select(".biz-name");
			Elements address = htmlDocument.select("address");
			Elements phone = htmlDocument.select(".biz-phone");
			Elements price = htmlDocument.select(".price-description");
			Elements category = htmlDocument.select(".category-str-list");
			Elements reviewCount = htmlDocument.select("span[itemprop=\"reviewCount\"]");
			Elements openHour = htmlDocument.select(".hours-table");
			Elements reviewsStar = htmlDocument.select("meta[itemprop=\"ratingValue\"]");
			
			System.out.println("Getting restaurant " + name.text());

			r.setBusinessId(id.attr("data-signup-object").split(":")[1]);
			r.setName(name.get(0).text());
			r.setAddress(address.get(0).text());
			r.setPhone(phone.text());
			r.setPrice(price.text());
			r.setReviewCount(reviewCount.text().isEmpty()? "0":reviewCount.get(0).text());
			r.setRating(reviewsStar.size() == 0? "" : reviewsStar.get(0).attr("content"));
			r.setCategory(category.text());
			r.setOpenHour(openHour.text().replaceAll("Open now ", "").replaceAll("Closed now ", ""));
			r.setYelpURL(baseLink+name.get(0).attr("href"));
			return r;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}	
	}
	
	// Parse yelp review list
	public List<Review> getReviews(Document htmlDocument, Restaurant restaurant){
		List<Review> reviewList = new LinkedList<>();
		int currIdx = 0;
		int reviewCount = Integer.parseInt(restaurant.getReviewCount());
		String link = restaurant.getYelpURL();
		
		while(currIdx < reviewCount){
			if(currIdx != 0){
				htmlDocument = ConnectionUtil.getDocument(link+"?start="+currIdx);
			}

			if(htmlDocument == null){
				writeErrorLog(pr, link+"?start="+currIdx);
				currIdx+=20;
				continue;
			}
			
			Elements reviews = htmlDocument.select(".review-content");
			Elements reviewsAuthorId = htmlDocument.select("div[data-signup-object]");
			Elements reviewsAuthor = htmlDocument.select("meta[itemprop=\"author\"]");
			Elements reviewsStar = htmlDocument.select("meta[itemprop=\"ratingValue\"]");
			Elements reviewsDate = htmlDocument.select("meta[itemprop=\"datePublished\"]");
			Elements reviewsContent = htmlDocument.select("p[itemprop=\"description\"]");
			Elements votes = htmlDocument.select(".count"); // useful funny, cool

			if(reviews.size() == 0){ break; }
			
			System.out.println("Getting review " + currIdx + " to " + (currIdx+reviews.size()));

			int voteIdx = 0;
			for(int i=0; i<reviews.size(); i++){
				String authorName = reviewsAuthor.get(i).attr("content");
				boolean isGhostUser = authorName.indexOf("Qype User") != -1;

				Review review = new Review(
						restaurant.getBusinessId(),
						String.valueOf(++currIdx),
						reviewsAuthorId.get(i).attr("data-signup-object").split(":")[1],
						authorName,
						reviewsStar.get(i+1).attr("content"),  // The first review star is the average of this restaurant
						reviewsDate.get(i).attr("content"),
						reviewsContent.get(i).text().replaceAll("\"",""),
						isGhostUser? "" : votes.get(3*voteIdx).text(),
						isGhostUser? "" : votes.get(3*voteIdx+1).text(),
						isGhostUser? "" : votes.get(3*voteIdx+2).text()
						);
				//System.out.println(review.getAuthor());
				if(!isGhostUser){ voteIdx++; }
				reviewList.add(review);
			}
			//currIdx += reviews.size();
		}
				
		return reviewList;
	}

	public void writeCSVFile(CSVPrinter printer, String[] o){
		try {
			printer.printRecord(o);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// If fail to get data, write a log
	private void writeErrorLog(PrintWriter pr, String link){
		pr.println(link);
		pr.flush();
	}
	
}
