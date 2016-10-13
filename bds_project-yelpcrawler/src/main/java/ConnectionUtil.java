import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by joyceyao on 4/3/2016.
 * Get html document from the URL
 */
public class ConnectionUtil {
    // Retract HTML content
    public static Document getDocument(String link){
        int retryCount = 10;
        org.jsoup.Connection connection = null;
        // Get connection
        while(connection == null && retryCount > 0){
            try{
                connection = Jsoup.connect(link);
            }catch(Exception e){
                retryCount--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println("retry connection..."+link);
            }
        }

        retryCount = 10;
        Document document = null;
        // Get document
        while(document == null  && retryCount > 0){
            try{
                document = connection.get();
            }catch(Exception e){
                retryCount--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println("retry document..."+link);
            }
        }

        return document;
    }
}
