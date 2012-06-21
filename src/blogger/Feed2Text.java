/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author davidherron
 */
public class Feed2Text {
    
    File feedFile = null;
    URL feedURL = null;
    SyndFeed thefeed = null;
    // XmlReader reader = null;
    String feedspec = null;
    
    boolean isFile = false;
    
    
    Feed2Text(URL _feedURL) {
        feedURL = _feedURL;
        feedFile = null;
        isFile = false;
    }
    
    Feed2Text(File _feedFile) {
        feedURL = null;
        feedFile = _feedFile;
        isFile = true;
    }
    
    
    Feed2Text(String _feedSpec) throws MalformedURLException {
        isFile = ! (_feedSpec.startsWith("http://"));
        if (isFile) {
            feedFile = new File(_feedSpec);
        } else {
            feedURL = new URL(_feedSpec);
        }
        feedspec = _feedSpec;
    }
    
    void get() throws IOException, IllegalArgumentException, FeedException {
        XmlReader reader;
        if (isFile) {
            reader = new XmlReader(feedFile);
        } else {
            reader = new XmlReader(feedURL);
        }
        SyndFeedInput input = new SyndFeedInput();
        thefeed = input.build(reader);
    }
    
    void printEntries() {
        System.out.println("");
        // ?? print out header information ??
        for (Object o : thefeed.getEntries()) {
            SyndEntryImpl entry = (SyndEntryImpl)o;
            System.out.println("title: " + entry.getTitle());
            System.out.println("date: " + entry.getPublishedDate().toString());
            System.out.println("url: " + entry.getLink());
//            for (Object olink : entry.getLinks()) {
//                SyndLinkImpl link = (SyndLinkImpl)olink;
//                System.out.println("url: " + link.getHref());
//            }
            SyndContent desc = entry.getDescription();
            if (desc != null && desc.getValue().length() > 0) System.out.println("description: " + desc.getValue());
            for (Object oencl : entry.getEnclosures()) {
                SyndEnclosure encl = (SyndEnclosure)oencl;
                System.out.println("enclosureUrl: " + encl.getUrl());
                System.out.println("enclusureMIME: " + encl.getType());
            }
            System.out.println("");
        }
    }
    
    public static void main(String[] args)
            throws MalformedURLException, IOException,
            IllegalArgumentException, FeedException
    {
        Feed2Text f2t = new Feed2Text(args[1]);
        f2t.get();
        f2t.printEntries();
    }
}
