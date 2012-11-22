/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogger;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.sun.syndication.io.FeedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author davidherron
 */
public class Main {
    
    public static void main(String[] args)
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException,
            MalformedURLException, IllegalArgumentException, FeedException,
            FileNotFoundException, ParseException, Exception
    {
        if (args.length > 0 && args[0].equals("fromtext")) FromText.main(args);
        else if (args.length > 0 && args[0].equals("counttext")) new FromText().countItems(args[1]);
        else if (args.length > 0 && args[0].equals("summary")) new FromText().generateSummary(args[1], args[2]);
        else if (args.length > 0 && args[0].equals("addtag")) new FromText().addTag(args);
        else if (args.length > 0 && args[0].equals("rmtags")) new FromText().rmTags(args);
        else if (args.length > 0 && args[0].equals("rmdate")) new FromText().rmDate(args);
        else if (args.length > 0 && args[0].equals("tagtitle")) new FromText().tagTitle(args);
        else if (args.length > 0 && args[0].equals("sorttitle")) new FromText().sortTitle(args);
        else if (args.length > 0 && args[0].equals("add2urilist")) new FromText().addToUriList(args);
        else if (args.length > 0 && args[0].equals("expungebyuri")) new FromText().expungeByUri(args);
        else if (args.length > 0 && args[0].equals("merge")) new FromText().merge(args);
        else if (args.length > 0 && args[0].equals("feed2text")) Feed2Text.main(args);
        else if (args.length > 0 && args[0].equals("listblogs")) ListBlogs.main(args);
        else if (args.length > 0 && args[0].equals("drupalcvt")) Converter.main(args);
        else if (args.length > 0 && args[0].equals("readertags")) Reader.listTags(args);
        else if (args.length > 0 && args[0].equals("tag")) Reader.tagEntries(args);
        else if (args.length > 0 && args[0].equals("starred")) Reader.starred(args);
        else if (args.length > 0 && args[0].equals("feedfinder")) Reader.feedFinder(args);
        else if (args.length > 0 && args[0].equals("feed")) Reader.feed(args);
        else {
            System.out.println("USAGE - Main fromtext authorName userName userPasswd blogId inputFile postSummaryFile notPostedFile");
            System.out.println("USAGE - Main feed2text feedUrl");
            System.out.println("USAGE - Main addtag inputFile outputFile tag");
            System.out.println("USAGE - Main rmtags inputFile outputFile");
            System.out.println("USAGE - Main rmdate inputFile outputFile");
            System.out.println("USAGE - Main tagtitle inputFile outputFile tag");
            System.out.println("USAGE - Main sortTitle inputFile outputFile");
            System.out.println("USAGE - Main add2urilist urifile txtfile");
            System.out.println("USAGE - Main expungebyuri urifile txtfile");
            System.out.println("USAGE - Main merge urifile outtxtfile infile1 infile2 infile3 ...");
            System.out.println("USAGE - Main counttext inputFile");
            System.out.println("USAGE - Main summary inputFile summaryFile");
            System.out.println("USAGE - Main listblogs userName userPasswd");
            System.out.println("USAGE - Main drupalcvt");
            System.out.println("USAGE - Main readertags userName userPassword");
            System.out.println("USAGE - Main tag userName userPassword tagName maxHours");
            System.out.println("USAGE - Main starred userName userPassword maxHours");
            System.out.println("USAGE - Main feedfinder userName userPassword query");
            System.out.println("USAGE - Main feed userName userPassword feedUrl maxHours");
        }
    }
    
}
