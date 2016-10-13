import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

/**
 * To extract  unique ID, latitude and longitude from Yelp, StreetEasy, and GooglePlace dataset,
 * to merge their ID with ACS data area code
 * Created by joyceyao on 4/21/2016.
 */
public class FileUtil {
    public static void main(String[] args){
        FileUtil.getStreetEasyCoordinate();
        //FileUtil.getGooglePlaceCoordinate();
    }

    // export GooglePlace data
    private static void getGooglePlaceCoordinate(){
        String outputFile = "D:\\BDS_HW\\project\\googlePlaceData\\googlePlace_idXYMapping.txt";
        String inputFile = "D:\\BDS_HW\\project\\googlePlaceData\\GooglePlacesData_OnlyNewYork.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(outputFile);
            String line = null;
            Type type = new TypeToken<Map<String, Object>>(){}.getType();

            while ((line =br.readLine()) != null && line.length() > 0) {
                int start = 0;
                int end = line.length();

                if(line.charAt(0) != '{'){ start++; }
                if(line.charAt(line.length()-1) != '}'){ end--; }
                if(start > end){ continue; }
                line = line.substring(start, end);
                if(line.length() <=2){ continue; }

                Gson gson = new Gson();
                Map<String, Object> restMap = gson.fromJson(line, type);
                String id = (String)restMap.get("id");

                LinkedTreeMap geoMap = (LinkedTreeMap)restMap.get("geometry");
                LinkedTreeMap locMap = (LinkedTreeMap)geoMap.get("location");
                Double longitude = (Double)locMap.get("lng");
                Double latitude = (Double)locMap.get("lat");
                //System.out.println("id="+id);
                writer.println(id+","+longitude+","+latitude);
            }
            br.close();
            writer.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // export StreetEasy data
    private static void getStreetEasyCoordinate(){
        String outputFile = "D:\\BDS_HW\\project\\streetEasy\\streetEasy_idXYMapping.txt";
        String inputFile = "D:\\BDS_HW\\project\\streetEasy\\streetEasyListings_3_2_2016_AND_4_22_2016_uniq.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(outputFile);
            String line = null;
            Type type = new TypeToken<Map<String, Object>>(){}.getType();

            while ((line =br.readLine()) != null && line.length() > 0) {
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(line));
                reader.setLenient(true);
                Map<String, Object> map = gson.fromJson(reader, type);

                Integer id = ((Double)map.get("id")).intValue();
                Double longitude = (Double)map.get("addr_lon");
                Double latitude = (Double)map.get("addr_lat");
                writer.println(id+","+longitude+","+latitude);
            }
            br.close();
            writer.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // export Yelp data
    private static void getYelpCoordinate(){
        String outputFile = "D:\\BDS_HW\\project\\yelpdata\\yelpPoints_noEmpty.txt";
        String restaurantPoints = "D:\\BDS_HW\\project\\yelpdata\\coordinate\\yelp_points.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(restaurantPoints));
            PrintWriter writer = new PrintWriter(outputFile);
            String line = br.readLine();

            while (line != null) {
                String[] data = line.split(",");
                if(data[1].isEmpty() || data[1].trim().length() == 0){ line = br.readLine(); continue; }
                writer.println(line);
                line = br.readLine();
            }
            br.close();
            writer.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
