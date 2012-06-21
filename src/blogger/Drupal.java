/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author davidherron
 */
public class Drupal {
    
    public class Data {
        String docTitle = null;
        String docLink = null;
        LinkedList<Element> entries = new LinkedList<Element>();
        LinkedList<EntryData> entryTuples = new LinkedList<EntryData>();
    }
    
    public class EntryData {
        String entryLink = null;
        String entryId = null;
        String entryTitle = null;
        String entryItemLink = null;
        String entryDescription = null;
        LinkedList<String> categories = new LinkedList<String>();
        String entryPublished = null;
    }

    public Drupal.Data scan(String urlToScan) throws ParserConfigurationException, SAXException, IOException {

	System.out.println("SCANNING:- "+ urlToScan);
        URL feed = new URL(urlToScan);
        URLConnection urlc = feed.openConnection();
        urlc.connect();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().parse(urlc.getInputStream());

        Element root = doc.getDocumentElement();
        
        Drupal.Data data = new Drupal.Data();

        Node child = null;
        for (child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
//            System.out.println(child.toString());
            Element e = null;
            if (child instanceof Element) {
                e = (Element) child;
                if (e.getTagName().equalsIgnoreCase("title")) {
                    data.docTitle = e.getTextContent();
                    continue;
                }
                if (e.getTagName().equalsIgnoreCase("link")) {
                    data.docLink = e.getAttribute("href");
                    continue;
                }
                if (e.getTagName().equalsIgnoreCase("entry")) {
                    data.entries.add(e);
                    continue;
                }
            }
        }

        System.out.println("SCANNING:- TITLE: " + data.docTitle);
        System.out.println("SCANNING:- LINK: " + data.docLink);

        for (Element entry : data.entries) {
            
            EntryData tuple = new EntryData();
            data.entryTuples.add(tuple);

//            System.out.println(entry.toString());
            for (child = entry != null ? entry.getFirstChild() : null;
                    child != null;
                    child = child != null ? child.getNextSibling() : null)
            {
                Element e = null;
//                System.out.println(child.toString());
                if (child instanceof Element) {
                    e = (Element) child;
                    if (e.getTagName().equalsIgnoreCase("link")) {
                        tuple.entryLink = e.getTextContent();
                        continue;
                    }
                    if (e.getTagName().equalsIgnoreCase("id")) {
                        tuple.entryId = e.getTextContent();
                        continue;
                    }

                    if (e.getTagName().equalsIgnoreCase("content")) {
                        for (Node child1 = e.getFirstChild(); child1 != null; child1 = child1.getNextSibling()) {
                            Element e1 = null;
//                            System.out.println(child1.toString());
                            if (child1 instanceof Element) {
                                e1 = (Element) child1;
                                if (e1.getTagName().equalsIgnoreCase("item")) {
                                    for (Node child2 = e1.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
                                        Element e2 = null;
//                                        System.out.println(child2.toString());
                                        if (child2 instanceof Element) {
                                            e2 = (Element) child2;
                                            if (e2.getTagName().equalsIgnoreCase("title")) {
                                                tuple.entryTitle = e2.getTextContent();
                                                continue;
                                            }
                                            if (e2.getTagName().equalsIgnoreCase("link")) {
                                                tuple.entryItemLink = e2.getTextContent();
                                                continue;
                                            }
                                            if (e2.getTagName().equalsIgnoreCase("description")) {
                                                tuple.entryDescription = e2.getTextContent();
                                                continue;
                                            }
                                            if (e2.getTagName().equalsIgnoreCase("category")) {
                                                tuple.categories.add(e2.getTextContent());
                                                continue;
                                            }
                                            if (e2.getTagName().equalsIgnoreCase("pubDate")) {
                                                tuple.entryPublished = e2.getTextContent();
                                                continue;
                                            }
                                            
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println("SCANNING:- ENTRY TITLE: "+ tuple.entryTitle);
            System.out.println("SCANNING:- ENTRY LINK: "+ tuple.entryLink);
            System.out.println("SCANNING:- ENTRY ID: "+ tuple.entryId);
            System.out.println("SCANNING:- ENTRY ITEM LINK: "+ tuple.entryItemLink);
            System.out.println("SCANNING:- ENTRY DESCRIPTION: "+ tuple.entryDescription);
            System.out.println("SCANNING:- ENTRY PUBLISHED: "+ tuple.entryPublished);
            for (String cat : tuple.categories) {
                System.out.println("SCANNING:- ENTRY CATEGORY: "+ cat);
            }
        }
        
        return data;
    }
}
