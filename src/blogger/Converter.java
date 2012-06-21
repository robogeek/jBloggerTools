/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import blogger.Drupal.EntryData;
import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.IEntry;
import com.google.gdata.data.Link;
import com.google.gdata.data.blogger.BlogEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.InvalidEntryException;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author davidherron
 */
public class Converter {

    static PrintStream log;

    static void writeDrushInstructions(String src, Link dest, String nodenum) {
        log.println("drush -u \"$1\" eval '$redirect = array("
                + "      \"rid\" => NULL,"
                + "      \"query\" => \"\","
                + "      \"fragment\" => \"\","
                + "      \"language\" => \"\","
                + "      \"type\" => 301,"
                + "      \"last_used\" => time(),"
                + "      \"source\" => \"" + src + "\","
                + "      \"redirect\" => \"" + dest.getHref() + "\","
                + "); "
                + "drupal_write_record(\"path_redirect\", $redirect); "
                + "$redirect = array("
                + "      \"rid\" => NULL,"
                + "      \"query\" => \"\","
                + "      \"fragment\" => \"\","
                + "      \"language\" => \"\","
                + "      \"type\" => 301,"
                + "      \"last_used\" => time(),"
                + "      \"source\" => \"node/" + nodenum + "\","
                + "      \"redirect\" => \"" + dest.getHref() + "\","
                + "); "
                + "drupal_write_record(\"path_redirect\", $redirect); "
                + "node_delete(" + nodenum + ");'");
    }

    static void migratePostsDrupal2Blogger(String authorName,
            String userName, String userPasswd, String blogId,
            Drupal.Data data)
            throws AuthenticationException, ParseException, ServiceException, IOException {

        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        service.setUserCredentials(userName, userPasswd);
        Blog blog = new Blog(service, blogId, authorName, userName);

        for (EntryData tuple : data.entryTuples) {
            DateTime publ = DateTime.parseRfc822(tuple.entryPublished);
            publ.setTzShift(0);
            IEntry post = blog.createPost(tuple.entryTitle,
                    tuple.entryDescription,
                    tuple.categories,
                    false, publ, publ);
            System.out.println("Successfully created draft post: ");
            System.out.println("entryLink: " + tuple.entryLink);
            System.out.println("entryId: " + tuple.entryId);
            System.out.println("entryTitle: " + tuple.entryTitle);
            System.out.println("entryItemLink: " + tuple.entryItemLink);
            PrintStuff.printEntry(post);

            String src = tuple.entryItemLink.substring(
                    tuple.entryItemLink.indexOf(":") + 3);
            writeDrushInstructions(
                    src.substring(src.indexOf("/") + 1),
                    ((BlogEntry) post).getHtmlLink(),
                    tuple.entryId.substring(tuple.entryId.lastIndexOf("/") + 1));

        }
    }

    public static void main(String[] args)
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException {

        log = new PrintStream("log.sh");
        //log.println("<?php");

        Drupal drupal = new Drupal();
//        Drupal.Data data = d.scan("file:///Users/davidherron/blogger/aa");

        try {
//            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "147431655452762453",
//                drupal.scan("http://old.wwwatts.net/atom.xml"));

// peaceguide
//            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "3621272226038322596",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://peaceguide.com/atom.xml"));


//////////        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//////////                "7752141524886431191",
//////////                drupal.scan("http://socialmedia4good.com/atom.xml"));

            // environment
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "6865375759111083",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

            // sustainable society
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "4954983368643086686",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

            // politics
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "1885175017079674264",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

            // photography
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "6129425628345426996",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/203/feed.xml"));

            // big brother
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "999473185938170160",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

            // 3wheelers
//            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                    "3598668628817026675",
//                    drupal.scan("file:///Users/davidherron/blogger/feed.xml"));
//                drupal.scan("http://www.7gen.com/atom2/1459/feed.xml"));

            // Mobile apps & Gizmos
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "21464128841651218",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/149/feed.xml"));

            // social media sanity
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "3563209132885947737",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/685/feed.xml"));

            // Freelance Writing
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "8744866096438582675",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/1902/feed.xml"));

            // Tutorials
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "7144077216524251090",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/208/feed.xml"));

            // electric race news snippets
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "8797946257552758107",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

            // Node.js
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "2502559522777854262",
//                drupal.scan("http://davidherron.com/atom/1928/feed.xml"));

            // Freelance Writing
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "8744866096438582675",
//                drupal.scan("http://davidherron.com/atom/500/feed.xml"));
            
            // Tutorials
//        migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "7144077216524251090",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://davidherron.com/atom/159/feed.xml"));

	    // energy and Growth
//            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "6909424726817970562",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));

	    // geekery
            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
                "7917804061695627262",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
                drupal.scan("http://davidherron.com/atom/152/feed.xml"));

	    // Green Transportation .info
//            migratePostsDrupal2Blogger("David Herron", "reikiman@gmail.com", "ellhnpezyfoxbqcn",
//                "7308431212965004892",
//                drupal.scan("file:///Users/davidherron/blogger/blogger/feed.xml"));
//                drupal.scan("http://greentransportationexaminer.com/atom/snippets.xml"));

        } catch (InvalidEntryException iee) {
            System.err.println("Caught exception " + iee.toString());
            iee.printStackTrace();
        }
        log.flush();
        log.close();
    }
}
