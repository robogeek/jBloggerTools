package blogger;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.Feed;
import com.google.gdata.data.Entry;

public class ListBlogs {

    static String userName   = null; // = "reikiman@gmail.com";
    static String userPasswd = null; // = "f000glip";

    public static void main(String[] args)
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException {

        userName   = args[1];
        userPasswd = args[2];
        
        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        service.setUserCredentials(userName, userPasswd);
        
          // Request the feed
        final URL feedUrl = new URL("http://www.blogger.com/feeds/default/blogs");
        Feed feed = service.getFeed(feedUrl, Feed.class);
    
        // Print the results
        System.out.println(feed.getTitle().getPlainText());
        for (int i = 0; i < feed.getEntries().size(); i++) {
            Entry entry = feed.getEntries().get(i);
            System.out.println(entry.getId() + "\t" + entry.getTitle().getPlainText());
        }
    }
}
