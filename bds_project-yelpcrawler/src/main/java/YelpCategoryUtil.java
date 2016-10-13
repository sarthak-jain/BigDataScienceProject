import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by joyceyao on 4/21/2016.
 */
public class YelpCategoryUtil {
    public static void main(String[] args){
       // YelpCategoryUtil.getYelpCategory();
        //FileUtil.getGooglePlaceCoordinate();
        YelpCategoryUtil.countRestaurant();
    }

    private static void getYelpCategory(){
        Map<String, Integer> map = new HashMap<>();
        List<String> list = new ArrayList<>();

        String outputFile = "D:\\BDS_HW\\project\\yelpdata\\data\\RestaurantCate.txt";
        BufferedReader br = null;
        int idx = 0;

        try {
            PrintWriter writer = new PrintWriter(outputFile);
            for(int i=1; i<=23; i++) {
                String inputFile = "D:\\BDS_HW\\project\\yelpdata\\data\\Restaurant" + i + ".csv";
                br = new BufferedReader(new FileReader(inputFile));
                String line = null;

                while ((line = br.readLine()) != null && line.length() > 0) {
                    String[] columns = line.split(",");
                    String cate = columns[7];
                    System.out.println(cate);

                    String[] allCates = cate.split(",");
                    for(String s: allCates){
                        if(map.containsKey(s)){ continue; }
                        map.put(s, idx++);
                        list.add(s);
                    }

                    //writer.println(id + "," + longitude + "," + latitude);
                }
                br.close();
            }
            writer.close();
            System.out.println("size="+list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void countRestaurant(){
        Set<String> set = new HashSet<>();
        try {
            for(int i=1; i<=23; i++) {
                String inputFile = "D:\\BDS_HW\\project\\yelpdata\\data\\Review" + i + ".csv";
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                String line = null;

                while ((line = br.readLine()) != null && line.length() > 0) {
                    String[] cols = line.split(",");
                    set.add(cols[0]);
                }
                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("num=" + set.size());
    }


}
