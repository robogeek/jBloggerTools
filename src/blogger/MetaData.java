
package blogger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author davidherron
 */
public class MetaData {
    
  public static final String METAFEED_URL = "http://www.blogger.com/feeds/default/blogs";

  public static final String FEED_URI_BASE = "http://www.blogger.com/feeds";
  public static final String POSTS_FEED_URI_SUFFIX = "/posts/default";
  public static final String COMMENTS_FEED_URI_SUFFIX = "/comments/default";
  
  // feedUri = FEED_URI_BASE + "/" + "147431655452762453";
  
  public static URL createPostFeedURL(String blogId) throws MalformedURLException {
    return new URL(FEED_URI_BASE +"/"+ blogId + POSTS_FEED_URI_SUFFIX);
  }
}
