/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.ICategory;
import com.google.gdata.data.IEntry;
import com.google.gdata.data.ILink;
import com.google.gdata.data.IPerson;
import com.google.gdata.data.Link;
import com.google.gdata.data.blogger.BlogEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author davidherron
 */
public class PrintStuff {
    
    public static void printLink(String label, ILink il) {
        Link l = null;
        if (il instanceof Link) {
            l = (Link) il;
        }
        String href = il != null ? il.getHref() : "";
        String title = l != null ? l.getTitle() : "";
        String rel = il != null ? il.getRel() : "";
        String etag = l != null ? l.getEtag() : "";
        System.out.println(label + title + " " + rel + " " + href + " " + etag);
    }

    public static void printEntry(IEntry ie) {
        BaseEntry be = null;
        BlogEntry bl = null;
        if (ie instanceof BaseEntry) {
            be = (BaseEntry) ie;
        }
        if (ie instanceof BlogEntry) {
            bl = (BlogEntry) ie;
        }
        System.out.println(ie.getTitle().getPlainText());
        System.out.println("EDIT " + (ie.getEdited() != null ? ie.getEdited().toUiString() : "")
                + " PUBL " + (ie.getPublished() != null ? ie.getPublished().toUiString() : ""));
        System.out.println("ETAG: " + ie.getEtag() + " ID: " + ie.getId());
        for (IPerson p : ie.getAuthors()) {
            System.out.println("AUTHOR: " + p.getName() + " " + p.getUri() + " " + p.getEmail());
        }
//      for (Person p : ie.getContributors()) {
//          System.out.println("CONTRIBUTOR: "+ p.getName() +" "+ p.getUri() +" "+ p.getEmail());
//      }
        System.out.println("CONTENT: " + (ie.getContent() != null ? ie.getContent().toString() : ""));
        for (ICategory c : ie.getCategories()) {
            System.out.println(c.getLabel() + " " + c.getTerm());
        }
        for (ILink l : ie.getLinks()) {
            printLink("LINK: ", l);
        }
        printLink("EDIT: ", ie.getEditLink());
        if (be != null) {
            printLink("HTML: ", be.getHtmlLink());
        }
        printLink("MEDIA: ", ie.getMediaEditLink());
        printLink("RESUMABLE-MEDIA: ", ie.getResumableEditMediaLink());
        if (be != null) {
            printLink("SELF: ", be.getSelfLink());
        }
        if (be != null) {
            try {
                System.out.println("TEXT CONTENT: "
                        + (be.getTextContent() != null && be.getTextContent().getContent() != null
                        ? be.getTextContent().getContent().getPlainText() : ""));
            } catch (Exception e) {
            }
        }
        if (be != null) {
            try {
                System.out.println("DRAFT? " + (be.getPubControl().isDraft() ? "draft" : "not draft"));
            } catch (Exception e) {
            }
        }
        if (bl != null) {
            printLink("ENTRY-POST: ", bl.getEntryPostLink());
        }
        if (bl != null) {
            printLink("FEED: ", bl.getFeedLink());
        }
        if (bl != null) {
            printLink("REPLIES: ", bl.getRepliesLink());
        }
    }

    public static void printAllPosts(BloggerService service, String blogId) throws ServiceException, IOException {
        // Request the feed
        URL feedUrl = MetaData.createPostFeedURL(blogId);
        Feed resultFeed = service.getFeed(feedUrl, Feed.class);

        // Print the results
        System.out.println(resultFeed.getTitle().getPlainText());
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
            Entry entry = resultFeed.getEntries().get(i);
            System.out.println("\t" + entry.getTitle().getPlainText());
            printEntry(entry);
        }
    }
}
