import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by joyceyao on 3/24/2016.
 */
public class LinksWriter {

    static String outputPath = "D:\\BDS_HW\\project\\yelpdata\\link\\";
    static String restaurantList = "D:\\BDS_HW\\project\\YelpMenu\\unique_restaurants.txt";
    static int interval = 10;

    public static void main(String[] args){
        int idx = 0;
        if(args.length > 0){ idx = Integer.parseInt(args[0]); }
        if(args.length > 1){ outputPath = args[1]; }
        if(args.length > 2){ restaurantList = args[2]; }
        if(args.length > 3){ interval = Integer.parseInt(args[3]); }
        LinkIterator itr = new LinkIterator(restaurantList, (idx-1)*interval);
        outputLinks(itr, idx++, interval);
    }

    public static void outputLinks(LinkIterator itr, int idx, int interval){
        String filename = outputPath+ "links" + idx + ".txt";
        int index = (idx-1)*interval;
        try {
            PrintWriter writer = new PrintWriter(filename);
            while(interval > 0 && itr.hasNext()){
                System.out.println("interval="+interval);
                String link = itr.next();
                Document htmlDocument = null;
                if(!link.isEmpty()){
                    htmlDocument = ConnectionUtil.getDocument(link);
                }
                if(htmlDocument == null){
                    writer.println(index++ + "," + " " + "," + link);
                    interval--;
                    continue;
                }

                String id = htmlDocument.select("a[data-signup-object]").attr("data-signup-object").split(":")[1];
                String location = htmlDocument.select("div[data-map-state]").attr("data-map-state");
                int startIdx = location.indexOf("latitude");
                int endIdx = location.indexOf("}", startIdx);
                String[] locs = location.substring(startIdx, endIdx).split(" ");

                writer.println(id + "," + locs[1].replaceAll(",", "") + "," + locs[3].replaceAll(",", "") );
                interval--;
                if(index%10 == 0){ writer.flush(); }
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
