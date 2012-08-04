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
        else if (args.length > 0 && args[0].equals("feed2text")) Feed2Text.main(args);
        else if (args.length > 0 && args[0].equals("listblogs")) ListBlogs.main(args);
        else if (args.length > 0 && args[0].equals("drupalcvt")) Converter.main(args);
        else {
            System.out.println("USAGE - Main fromtext authorName userName userPasswd blogId inputFile postSummaryFile notPostedFile");
            System.out.println("USAGE - Main feed2text feedUrl");
            System.out.println("USAGE - Main counttext inputFile");
            System.out.println("USAGE - Main summary inputFile summaryFile");
            System.out.println("USAGE - Main listblogs userName userPasswd");
            System.out.println("USAGE - Main drupalcvt");
        }
    }
    
}
