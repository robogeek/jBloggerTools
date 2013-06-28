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
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import org.jdom.Element;

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
        input.setPreserveWireFeed(true);
        thefeed = input.build(reader);
    }
    
    void printEntries(String outfn)
        throws java.io.FileNotFoundException
    {
        PrintStream out = System.out;
        if (outfn != null && ! outfn.equals("-")) {
            out = new PrintStream(outfn);
        }
        out.println("");
        // ?? print out header information ??
        for (Object o : thefeed.getEntries()) {
            SyndEntryImpl entry = (SyndEntryImpl)o;
            if (!allowEntry(entry)) continue;
            
            Object oItem    = entry.getWireEntry();
            Item   rssItem  = null;
            Entry  atomItem = null;
            
            // Flags for sites that get special treatment
            boolean isBusinessInsider = false;
            
            Date publ = entry.getPublishedDate();
            if (publ == null) publ = new Date();
            
//            if (oItem != null) System.out.println("wireEntry: " + oItem.toString()); 
            if (oItem instanceof Item)  rssItem  = (Item)  oItem;
            if (oItem instanceof Entry) atomItem = (Entry) oItem;
            out.println("title: " + Utils.cleanTitle(entry.getTitle())); 
            out.println("date: " + Long.toString(publ.getTime()) +" "+ Long.toString(publ.getTimezoneOffset()));
                // entry.getPublishedDate().toString());
            out.println("url: " + entry.getLink());
            out.println("uri: " + entry.getUri());
            if (entry.getUri().contains("www.businessinsider.com")) isBusinessInsider = true;
            for (Object oo : entry.getCategories()) {
                SyndCategoryImpl category = (SyndCategoryImpl) oo;
                String tagName = category.getName();
                if (tagName.startsWith("http:")) continue;
                out.println("tag: " + tagName);
            }
            if (rssItem != null && rssItem.getGuid() != null) {
                out.println("rssguid: " + Utils.removeNewLines(rssItem.getGuid().toString()));
                // out.println("rssDescription: " + rssItem.getDescription());
            }
            if (atomItem != null) {
                out.println("atomId: " + atomItem.getId());
                out.println("atomSummary: " 
                        + (atomItem.getSummary() != null ? atomItem.getSummary() : ""));
//                for (Object oModule : atomItem.getModules()) {
//                    out.println("atomModule: " + oModule.toString());
//                }
//                out.println("atomMarkup: " + atomItem.getForeignMarkup().toString());
            }
//            for (Object oModule : entry.getModules()) {
//                out.println("module: " + oModule.toString());
//            }
            for (Object olink : entry.getLinks()) {
                SyndLinkImpl link = (SyndLinkImpl)olink;
                String href = link.getHref();
                out.println("link: " + href);
                if (href.matches(".*www.youtube.com/watch.*")) {
                    out.println("youtubeUrl: " + href);
                }
            }
            // System.out.println("markup: " + entry.getForeignMarkup().toString());
            List mList = (List)entry.getForeignMarkup();
            for (Object oMarkup : mList) {
//                System.out.println("markup: " + oMarkup.toString());
                if (oMarkup instanceof Element) {
                    Element elem = (Element)oMarkup;
                    if (elem.getNamespace().getPrefix().equals("media") && elem.getName().equals("group")) {
                        for (Object oChild : elem.getChildren()) {
//                            System.out.println("media:group:child " + oChild.toString());
                            if (oChild instanceof Element) {
                                Element chElem = (Element)oChild;
                                if (chElem.getName().equals("thumbnail") && chElem.getNamespacePrefix().equals("media")) {
                                    out.println("mediaThumbnail: url=" + chElem.getAttributeValue("url") 
                                            +" height="+ chElem.getAttributeValue("height") 
                                            +" width="+ chElem.getAttributeValue("width") +" ");
                                }
                                if (chElem.getName().equals("title") && chElem.getNamespacePrefix().equals("media")) {
                                    out.println("mediaTitle: " + chElem.getText());
                                }
                                if (chElem.getName().equals("description") && chElem.getNamespacePrefix().equals("media")) {
                                    String md = Utils.cleanup(Utils.removeNewLines(chElem.getText()));
                                    out.println("mediaDescription: " + md);
                                }
                                if (chElem.getName().equals("credit") && chElem.getNamespacePrefix().equals("media")) {
                                    out.println("mediaCredit: " + chElem.getText());
                                }
                            }
                        }
                    }
//                    out.println("element-name: "+ elem.getName() +" namespace: "+ elem.getNamespace().toString());
//                    out.println("\t"+ elem.getValue());
                }
            }
            SyndContent desc = entry.getDescription();
            if (desc != null && desc.getValue().length() > 0) {
                String d = "";
                if (isBusinessInsider)
                    d = Utils.smallifyDescription(Utils.removeNewLines(desc.getValue()));
                else
                    d = Utils.cleanup(Utils.removeNewLines(desc.getValue()));
                out.println("description: " + d);
            }
            for (Object oencl : entry.getEnclosures()) {
                SyndEnclosure encl = (SyndEnclosure)oencl;
                out.println("enclosureUrl: " + encl.getUrl());
                out.println("enclusureMIME: " + encl.getType());
            }
            out.println("");
        }
        if (out != System.out) {
            out.close();
        }
    }
    
    boolean allowEntry(SyndEntryImpl entry) {
        
        // check maxAge against entry published date 
        if (maxAge != null) {
            Date then = entry.getPublishedDate();
            Date now = new Date();
            if (then == null)
                then = now;
            
            if ((now.getTime() - then.getTime()) > maxAge)
                return false;
        }
                
        return true;
    }
    
    Long maxAge = null;
    
    void setMaxAge(String max) {
        try { setMaxAge(new Long(max)); } catch (Exception e) { }
    }
    
    void setMaxAge(Long max) {
        maxAge = max * 1000 * 3600;
    }
    
    public static void main(String[] args)
            throws MalformedURLException, IOException,
            IllegalArgumentException, FeedException
    {
        HttpURLConnection.setFollowRedirects(true);
        Feed2Text f2t = new Feed2Text(args[1]);
        if (args.length > 2) {
            f2t.setMaxAge(args[2]);
        }
        f2t.get();
        f2t.printEntries(args.length > 3 ? args[3] : null);
    }
}
