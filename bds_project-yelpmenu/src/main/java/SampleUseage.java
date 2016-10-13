/**
 * Created by itawfik on 3/22/16.
 */
public class SampleUseage {
    public static void main(String[] args) {
        LinkIterator itr = new LinkIterator("unique_restaurants.txt", 19);

        while(itr.hasNext()){
            String link = itr.next();
            System.out.println(link);
        }

    }
}
