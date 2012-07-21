/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.*;
import com.google.gdata.data.blogger.BlogEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author davidherron
 */
public class Blog {

    BloggerService service = null;
    String blogId = null;
    String authorName = null;
    String userName = null;

    public Blog(BloggerService service, String blogId, String authorName, String userName) {
        this.service = service;
        this.blogId = blogId;
        this.authorName = authorName;
        this.userName = userName;
    }

    /**
     * Creates a new post on a blog. The new post can be stored as a draft or
     * published based on the value of the isDraft parameter. The method creates an
     * Entry for the new post using the title, content, authorName and isDraft
     * parameters. Then it uses the given GoogleService to insert the new post. If
     * the insertion is successful, the added post will be returned.
     * 
     * @param title Text for the title of the post to create.
     * @param content Text for the content of the post to create.
     * @param isDraft True to save the post as a draft, False to publish the post.
     * @return An Entry containing the newly-created post.
     * @throws ServiceException If the service is unable to handle the request.
     * @throws IOException If the URL is malformed.
     */
    public IEntry createPost(String title, String content, LinkedList<String> categories, Boolean isDraft, DateTime published, DateTime edited)
            throws ServiceException, IOException {
        // Create the entry to insert
        BlogEntry entry = new BlogEntry();
	System.out.println("NEW: "+ title);
        entry.setTitle(new PlainTextConstruct(title));
        entry.setContent(new PlainTextConstruct(content));
        if (published != null) {
            entry.setPublished(published);
        }
        if (edited != null) {
            entry.setEdited(edited);
        }
        entry.getAuthors().add(new Person(authorName, null, userName));
        entry.setDraft(isDraft);
        entry.setCanEdit(true);

	int count = 0;
        for (String catname : categories) {
	    if (count++ > 19) continue;
            Category category = new Category();
            category.setScheme("http://www.blogger.com/atom/ns#");
            category.setTerm(catname);
	    System.out.println("NEW CATEGORY: "+ catname);
            entry.getCategories().add(category);
        }

        // Ask the service to insert the new entry
        URL postUrl = MetaData.createPostFeedURL(blogId);
        
        boolean posted;
        IEntry ientry = null;
        do {
            try {
                ientry = service.insert(postUrl, entry);
                // we only get here if there are no exceptions thrown
                // indicating SUCCESSFUL posting 
                posted = true;  // in case posted became false because of an exception
            } catch (com.google.gdata.util.ServiceException se) {
                System.err.println("Caught exception " + se.getMessage());
                System.err.println("Response body " + se.getResponseBody());
                System.err.println("Code name " + se.getCodeName());
                System.err.println("Debug info " + se.getDebugInfo());
                System.err.println("Domain name " + se.getDomainName());
                System.err.println("Extended help " + se.getExtendedHelp());
                System.err.println("Internal reason " + se.getInternalReason());
                System.err.println("Location " + se.getLocation());
                System.err.println("string: " + se.toString());
                // se.printStackTrace();
                if (se.getResponseBody().contains("exceeded rate limit")) {
                    System.err.println("ERROR ERROR - you've exceeded the rate limit of 50 postings");
                    throw se;
                }
                posted = false;
                try {
                    // The exception may be due to blogger being overloaded
                    // sleep to give time for blogger to catch up .. maybe ..?
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        } while (!posted);
        return ientry;
    }

    public IEntry createPost(String title, String content, Boolean isDraft)
            throws ServiceException, IOException {
        return createPost(title, content, isDraft);
    }

    public void deletePost(String editLinkHref) throws ServiceException, IOException {
        URL deleteUrl = new URL(editLinkHref);
        service.delete(deleteUrl);
    }

    public void printAllPosts() throws ServiceException, IOException {
        PrintStuff.printAllPosts(service, blogId);
    }
}
