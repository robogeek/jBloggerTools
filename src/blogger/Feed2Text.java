/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
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
            Object oItem    = entry.getWireEntry();
            Item   rssItem  = null;
            Entry  atomItem = null;
            if (oItem instanceof Item)  rssItem  = (Item)  oItem;
            if (oItem instanceof Entry) atomItem = (Entry) oItem;
            System.out.println("title: " + entry.getTitle()); 
            System.out.println("date: " + Long.toString(entry.getPublishedDate().getTime()) 
                    +" "+ Long.toString(entry.getPublishedDate().getTimezoneOffset()));
                // entry.getPublishedDate().toString());
            System.out.println("url: " + entry.getLink());
            System.out.println("uri: " + entry.getUri());
            if (oItem != null) System.out.println("wireEntry: " + oItem.toString());
            if (rssItem != null) {
                System.out.println("rssguid: " + rssItem.getGuid());
            }
            if (atomItem != null) {
                System.out.println("atomid: " + atomItem.getId());
            }
//            for (Object olink : entry.getLinks()) {
//                SyndLinkImpl link = (SyndLinkImpl)olink;
//                System.out.println("url: " + link.getHref());
//            }
            SyndContent desc = entry.getDescription();
            if (desc != null && desc.getValue().length() > 0) 
                System.out.println("description: " + removeNewLines(desc.getValue()));
            for (Object oencl : entry.getEnclosures()) {
                SyndEnclosure encl = (SyndEnclosure)oencl;
                System.out.println("enclosureUrl: " + encl.getUrl());
                System.out.println("enclusureMIME: " + encl.getType());
            }
            System.out.println("");
        }
    }
    
    String removeNewLines(String s) {
        while (s.indexOf("\n") >= 0) {
            String n = s.substring(0, s.indexOf("\n")) + s.substring(s.indexOf("\n") + 1);
            s = n;
        }
        return s;
    }
    
    public static void main(String[] args)
            throws MalformedURLException, IOException,
            IllegalArgumentException, FeedException
    {
        HttpURLConnection.setFollowRedirects(true);
        Feed2Text f2t = new Feed2Text(args[1]);
        f2t.get();
        f2t.printEntries();
    }
}
