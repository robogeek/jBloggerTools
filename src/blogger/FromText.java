package blogger;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.IEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

// TBD: Need tool to generate an input.txt from an RSS feed, checking w/ a blog to remove duplicates

public class FromText {
    
    class Row {
        public Row() {
            this.categories     = new LinkedList<String>();
            this.title          = null;
            this.date           = null;
            this.url            = null;
            this.uri            = null;
            this.id             = null;
            this.description    = null;
            this.image          = null;
            this.youtubeUrl     = null;
            this.enclosureUrl   = null;
            this.enclusureMIME  = null;
        }
        LinkedList<String> categories;
        String title;
        String url;
        String uri;
        String id;
        String date;
        String description;
        String image;
        String youtubeUrl;
        String enclosureUrl;
        String enclusureMIME;
        
        public boolean isEmpty() {
            if (title == null || title.length() <= 0
             || url   == null || url.length() <= 0
             || description == null || description.length() <= 0) {
                return true;
            } else {
                return false;
            }
        }
        
        @Override
        public String toString() {
            String rv = "";
            for (String tag : categories) {
                rv += "tag: "+ tag +"\n";
            }
            rv += "title: "+          title          +"\n";
            rv += "url: "+            url            +"\n";
            rv += "uri: "+            uri            +"\n";
            rv += "id: "+             id            +"\n";
            rv += "date: "+           date           +"\n";
            rv += "description: "+    description    +"\n";
            rv += "image: "+          image          +"\n";
            rv += "youtubeUrl: "+     youtubeUrl     +"\n";
            rv += "enclosureUrl: "+   enclosureUrl   +"\n";
            rv += "enclusureMIME: "+  enclusureMIME  +"\n";
            return rv;
        }
    }
    
    LinkedList<Row> rows   = null;
    LinkedList<Row> posted = null;
    
    void parseText(File inputFile)
        throws java.io.FileNotFoundException, java.io.IOException
    {
        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Row thisRow;
        for (thisRow = new Row(); (line = br.readLine()) != null; /*thisRow = new Row()*/) {
            line = line.trim();
            if (line.length() <= 0) {
                // - process previously gathered data
                if (! thisRow.isEmpty()) {
                    rows.addLast(thisRow);
                    thisRow = new Row();
                }
                continue;
            }
            int colon = line.indexOf(":");
            if (colon > 0) {
                String name = line.substring(0, colon);
                String val  = line.substring(colon+1).trim();
//                if (name.equals("authorName"))     { authorName             = val; }
//                if (name.equals("userName"))       { userName               = val; }
//                if (name.equals("userPasswd"))     { userPasswd             = val; }
//                if (name.equals("blogId"))         { blogId                 = val; }
                if (name.equals("tag"))            { thisRow.categories.add(val);  }
                if (name.equals("title"))          { thisRow.title          = val; }
                if (name.equals("url"))            { thisRow.url            = val; }
                if (name.equals("uri"))            { thisRow.uri            = val; }
                if (name.equals("rssguid"))        { thisRow.id             = val; }
                if (name.equals("atomid"))         { thisRow.id             = val; }
                if (name.equals("date"))           { thisRow.date           = val; }
                if (name.equals("description"))    { thisRow.description    = val; }
                if (name.equals("image"))          { thisRow.image          = val; }
                if (name.equals("youtubeUrl"))     { thisRow.youtubeUrl     = val; }
                if (name.equals("enclosureUrl"))   { thisRow.enclosureUrl   = val; }
                if (name.equals("enclusureMIME"))  { thisRow.enclusureMIME  = val; }
            }
        }
        fr.close();
    }
    
    void postIndividually(Blog blog)
        throws com.google.gdata.util.ServiceException, java.io.IOException,
            java.net.MalformedURLException, ParseException
    {
        for (Row row = rows.peekLast(); row != null; row = rows.peekLast()) {
            DateTime publ;
            
            if (row.date != null && row.date.length() > 0) {
                String[] dates = row.date.split(" ");
                Date date = new Date(new Long(dates[0]));
                publ = new DateTime(date);
                publ.setTzShift(new Integer(dates[1]));
            } else {
                publ = DateTime.now();
            }
            // ?? publ.setTzShift(0);
            String description = "";
            description += "<p>"+ row.description +"</p>\n";
            if (row.image != null && row.image.length() > 0) {
                description += "<p><img width=450 src=\""+ row.image +"\"/></p>\n";
            }
            if (row.enclosureUrl != null && row.enclosureUrl.length() > 0) {
                description += generateEnclosurePlayer(row.enclosureUrl, row.enclusureMIME);
            }
            if (row.youtubeUrl != null && row.youtubeUrl.length() > 0) {
                 description += generateYoutubeIframe(row.youtubeUrl) +"\n";
            }
            description += "<p><span style=\"font-size: x-small;\"><a href=\""+ row.url +"\">"+ row.url +"</a></span></p>\n";
            
            IEntry post = blog.createPost(row.title,
                    description,
                    row.categories,
                    false, publ, publ);
            
            rows.remove(row);
            posted.addLast(row);
        }
    }
    
    void postSummary()
        throws java.net.MalformedURLException
    {
        for (Row row : posted) {
            System.out.println("");
            System.out.println("<h2>"+ row.title +"</h2>");
            System.out.println("<p>"+ row.description +"</p>");
            if (row.image != null && row.image.length() > 0) {
                System.out.println("<p><img width=450 src=\""+ row.image +"\"/></p>");
            }
            if (row.youtubeUrl != null && row.youtubeUrl.length() > 0) {
                 System.out.println(generateYoutubeIframe(row.youtubeUrl));
            }
            // TBD enclosureUrl enclusureMIME
            System.out.println("<p><span style=\"font-size: x-small;\"><a href=\""+ row.url +"\">"+ row.url +"</a></span></p>");
            System.out.println("");
        }
    }
    
    // http://www.youtube.com/watch?v=c1wCszOpeYk&feature=g-vrec
    // <iframe width="450" height="253" src="http://www.youtube.com/embed/c1wCszOpeYk" frameborder="0" allowfullscreen></iframe>
    String generateYoutubeIframe(String youtubeUrl)
        throws java.net.MalformedURLException
    {
        URL url = new URL(youtubeUrl);
        String qry = url.getQuery();
        if (qry == null || qry.length() <= 0) {
            return "";
        }
        String[] splits = qry.split("&");
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].startsWith("v=")) {
                String code = splits[i].substring(2);
                return "<iframe width=\"450\" height=\"253\" src=\"http://www.youtube.com/embed/"+ code +"\" frameborder=\"0\" allowfullscreen></iframe>";
            }
        }
        return "";
    }
    
    String generateEnclosurePlayer(String enclUrl, String enclType) {
        
        // TBD finish this
        return "";
    }
    
    String authorName = null; // "David Herron";
    String userName   = null; // "reikiman@gmail.com";
    String userPasswd = null; // "ellhnpezyfoxbqcn";
    
    String blogId = null; // "4358407941295111084"; // "3621272226038322596";
    String inputFile = null; // "input.txt";
    
    void run(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, java.io.FileNotFoundException,
            java.net.MalformedURLException, ParseException {
        
        authorName = args[1];
        userName   = args[2];
        userPasswd = args[3];
        blogId     = args[4];
        inputFile  = args[5];
        
//        System.out.println("author name: " + authorName);
//        System.out.println("user name: " + userName);
//        System.out.println("user password: " + userPasswd);
//        System.out.println("blog ID: " + blogId);
//        System.out.println("input file: " + inputFile);
        
        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        service.setUserCredentials(userName, userPasswd);
        Blog blog = new Blog(service, blogId, authorName, userName);
        rows   = new LinkedList<Row>();
        posted = new LinkedList<Row>();
        parseText(new File(inputFile)); 
        postIndividually(blog);
        postSummary();
        
    }
    
    public static void main(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, 
            java.io.FileNotFoundException, MalformedURLException, ParseException {
        new FromText().run(args);
    }
}

