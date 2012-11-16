/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.IEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 *
 * @author davidherron
 */
public class Blogger {

    /**
     * Parses the metafeed to get the blog ID for the authenticated user's default
     * blog.
     * 
     * @param myService An authenticated GoogleService object.
     * @return A String representation of the blog's ID.
     * @throws ServiceException If the service is unable to handle the request.
     * @throws IOException If the URL is malformed.
     */
    private static String getBlogId(BloggerService myService)
            throws ServiceException, IOException {
        // Get the metafeed
        final URL feedUrl = new URL(MetaData.METAFEED_URL);
        Feed resultFeed = myService.getFeed(feedUrl, Feed.class);

        // If the user has a blog then return the id (which comes after 'blog-')
        if (resultFeed.getEntries().size() > 0) {
            Entry entry = resultFeed.getEntries().get(0);
            return entry.getId().split("blog-")[1];
        }
        throw new IOException("User has no blogs!");
    }

    /**
     * Prints a list of all the user's blogs.
     * 
     * @param myService An authenticated GoogleService object.
     * @throws ServiceException If the service is unable to handle the request.
     * @throws IOException If the URL is malformed.
     */
    public static void printUserBlogs(BloggerService myService)
            throws ServiceException, IOException {

        // Request the feed
        final URL feedUrl = new URL(MetaData.METAFEED_URL);
        Feed resultFeed = myService.getFeed(feedUrl, Feed.class);

        // Print the results
        System.out.println(resultFeed.getTitle().getPlainText());
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            IEntry entry = resultFeed.getEntries().get(i);
            System.out.println("\t" + entry.getId() + " "
                    + entry.getTitle().getPlainText() + " "
                    + entry.getKind());

            //printEntry(entry);
        }
        System.out.println();
    }

    /*
     * David Herron's Blogs
    tag:blogger.com,1999:user-14564128110588027532.blog-7308431212965004892 Green Transportation .Info null
    tag:blogger.com,1999:user-14564128110588027532.blog-1691273204539341036 The Long Tail Pipe null
    tag:blogger.com,1999:user-14564128110588027532.blog-6909424726817970562 Energy and Growth null
    tag:blogger.com,1999:user-14564128110588027532.blog-147431655452762453 EV Voices null
    tag:blogger.com,1999:user-14564128110588027532.blog-5778141573182199546 Gasoholics null
    tag:blogger.com,1999:user-14564128110588027532.blog-7752141524886431191 Social Media for Good null
    tag:blogger.com,1999:user-14564128110588027532.blog-6059727809033894995 The Green Web null
    tag:blogger.com,1999:user-14564128110588027532.blog-5750429371300413084 CMS Try Out null
     */
    public static void run(BloggerService service, Blog blog) throws ServiceException, IOException {

        // Authenticate using ClientLogin
        printUserBlogs(service);

        // Demonstrate how to create a draft post.
        Date publDate = new Date();
        publDate.setYear(publDate.getYear() - 1);
        DateTime publ = new DateTime(publDate);
        publ.setTzShift(new Integer(-8));
        IEntry post = blog.createPost("Snorkling in Aruba DRAFT",
                "<p>We had so much fun snorkling in Aruba<p><p>DRAFT</p>",
                null,
                false, publ, DateTime.now());
        System.out.println("Successfully created draft post: ");
        PrintStuff.printEntry(post);
        
        blog.printAllPosts();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ServiceException, IOException {
        String authorName = "David Herron";
        String userName = "reikiman@gmail.com";
        String userPasswd = "f000glip";
        String blogId = "147431655452762453";
        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        service.setUserCredentials(userName, userPasswd);
        Blog blog = new Blog(service, blogId, authorName, userName);
        run(service, blog);
    }
    
}
