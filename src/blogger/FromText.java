package blogger;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.IEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

// TODO - Separate the Row class, and parseText into it's own class, enabling other uses of parsing the data file

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
             /*|| description == null || description.length() <= 0*/) {
                return true;
            } else {
                return false;
            }
        }
        
        private static final String tmpl =
            "title: @title@\n"
           +"url: @url@\n"
           +"uri: @uri@\n"
           +"id: @id@\n"
           +"date: @date@\n"
           +"description: @description@\n"
           +"image: @image@\n"
           +"youtubeUrl: @youtubeUrl@\n"
           +"enclosureUrl: @enclosureUrl@\n"
           +"enclusureMIME: @enclusureMIME@\n"
           +"@tags@";
        private static final String tagTmpl = "tag: @tag@\n";
        
        @Override
        public String toString() {
            String tags = "";
            for (String tag : categories) {
                tags += tagTmpl.replaceAll("@tag@", tag);
            }
            return tmpl
                    .replaceAll("@title@", (title != null) ? title : "")
                    .replaceAll("@url@", (url != null) ? url : "")
                    .replaceAll("@uri@", (uri != null) ? uri : "")
                    .replaceAll("@id@", (id != null) ? id : "")
                    .replaceAll("@date@", (date != null) ? date : "")
                    .replaceAll("@image@", (image != null) ? image : "")
                    .replaceAll("@description@", (description != null) ? description : "")
                    .replaceAll("@youtubeUrl@", (youtubeUrl != null) ? youtubeUrl : "")
                    .replaceAll("@enclosureUrl@", (enclosureUrl != null) ? enclosureUrl : "")
                    .replaceAll("@enclusureMIME@", (enclusureMIME != null) ? enclusureMIME : "")
                    .replaceAll("@tags@", (tags != null) ? tags : "");
        }
    }
    
    LinkedList<Row> rows   = null;
    LinkedList<Row> posted = null;
    
    private void parseText(File inputFile)
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
//                    System.out.println("ADDED " + thisRow.toString());
                    thisRow = new Row();
                }
                continue;
            }
            //System.out.println(line);
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
    
//    String removeCR(String s) {
//        while (s.endsWith("\r")) {
//            String n = s.substring(0, s.indexOf("\r")); // + s.substring(s.indexOf("\r") + 1);
//            s = n;
//        }
//        return s;
//    }
    
    private void postIndividually(Blog blog)
        throws com.google.gdata.util.ServiceException, java.io.IOException,
            java.net.MalformedURLException, ParseException
    {
        for (Row row = rows.peekLast(); row != null; row = rows.peekLast()) {
            DateTime publ;
            
//            System.out.println(row.toString());
            if (row.date != null && row.date.length() > 0) {
                String[] dates = row.date.split(" ");
                Date date = new Date(new Long(dates[0]));
                publ = new DateTime(date);
                publ.setTzShift(new Integer(dates[1]));
            } else {
                publ = DateTime.now();
            }
            
            String description = postBodyTemplate
                    .replaceAll("@description@", row.description)
                    .replaceAll("@images@", 
                        (row.image != null && row.image.length() > 0)
                        ? imageTemplate.replaceAll("@imageurl@", row.image)
                        : ""
                    )
                    .replaceAll("@youtube@", 
                        (row.youtubeUrl != null && row.youtubeUrl.length() > 0)
                        ? generateYoutubeIframe(row.youtubeUrl)
                        : ""
                    )
                    .replaceAll("@url@", row.url)
                    .replaceAll("@enclosures@", 
                        (row.enclosureUrl != null && row.enclosureUrl.length() > 0)
                        ? generateEnclosurePlayer(row.enclosureUrl, row.enclusureMIME)
                        : ""
                    );
            IEntry post = blog.createPost(row.title,
                    description,
                    row.categories,
                    false, publ, publ);
            
            rows.remove(row);
            posted.addLast(row);
        }
    }
    
    static final String postBodyTemplate =
            "<p>@description@</p>\n"
           +"@images@\n"
           +"@youtube@\n"
           +"@enclosures@\n"
           +"<p><a href=\"@url@>@url@</a></p>\n";
    
    static final String postTemplate =
            "<h2>@title@</h2>\n"
           +"<p>@description@</p>\n"
           +"@images@\n"
           +"@youtube@\n"
           +"@enclosures@\n"
           +"<p><span style=\"font-size: x-small;\"><a href=\"@url@>@url@</a></span></p>\n";
    
    static final String postTemplate2 =
            "<h2>@title@</h2>\n"
           +"<p>@description@</p>\n"
           +"@images@\n"
           +"@youtube@\n"
           +"@enclosures@\n"
           +"<p><a href=\"@url@>@url@</a></p>\n";
    
    static final String enclosureAudioTemplate = ""; // TODO audio enclosure template
    static final String enclosureVideoTemplate = ""; // TODO video enclosure template
    
    static final String imageTemplate = "<p><img width=450 src=\"@imageurl@\"/></p>";
    
    static final String youtubeTemplate = "<iframe width=\"450\" height=\"253\" src=\"http://www.youtube.com/embed/@code@\" frameborder=\"0\" allowfullscreen></iframe>";
    
    private void postSummary(String postedFile)
        throws java.net.MalformedURLException, FileNotFoundException
    {
        PrintStream out = new PrintStream(postedFile);
        for (Row row : posted) {
            String img = "";
            if (row.image != null && row.image.length() > 0) {
                img = imageTemplate.replaceAll("@imageurl@", row.image);
            }
            out.println("");
            out.println(
                    postTemplate.replaceAll("@title@", row.title)
                        .replaceAll("@description@", row.description)
                        .replaceAll("@url@", row.url)
                        .replaceAll("@images@", img)
                        .replaceAll("@youtube@", 
                            (row.youtubeUrl != null && row.youtubeUrl.length() > 0)
                            ? generateYoutubeIframe(row.youtubeUrl)
                            : "")
                        .replaceAll("@enclosures@", 
                            (row.enclosureUrl != null && row.enclosureUrl.length() > 0)
                            ? generateEnclosurePlayer(row.enclosureUrl, row.enclusureMIME)
                            : ""
                        )
                    );
            out.println("");
        }
        out.close();
    }
    
    private void notPostedSummary(String notPostedFile) throws FileNotFoundException {
        PrintStream out = new PrintStream(notPostedFile);
        for (Row row : rows) {
            out.println("");
            out.println(row.toString());
            out.println("");
        }
        out.close();
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
                return youtubeTemplate.replaceAll("@code@", code);
            }
        }
        return "";
    }
    
    String generateEnclosurePlayer(String enclUrl, String enclType) {
        
        // TODO implement generateEnclosurePlayer
        return "";
    }
    
    String authorName = null; // "David Herron";
    String userName   = null; // "reikiman@gmail.com";
    String userPasswd = null; // "ellhnpezyfoxbqcn";
    
    String blogId = null; // "4358407941295111084"; // "3621272226038322596";
    String inputFile = null; // "input.txt";
    
    String postedFile = null;
    String notPostedFile = null;
    
    void run(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, java.io.FileNotFoundException,
            java.net.MalformedURLException, ParseException {
        
        authorName = args[1];
        userName   = args[2];
        userPasswd = args[3];
        blogId     = args[4];
        inputFile  = args[5];
        
        if (args.length >= 7) postedFile    = args[6];
        if (args.length >= 8) notPostedFile = args[7];
        
        System.out.println("author name: " + authorName);
        System.out.println("user name: " + userName);
        System.out.println("user password: " + userPasswd);
        System.out.println("blog ID: " + blogId);
        System.out.println("input file: " + inputFile);
        System.out.println("posted file: " + postedFile);
        System.out.println("not posted file: " + notPostedFile);
        
        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        service.setUserCredentials(userName, userPasswd);
        Blog blog = new Blog(service, blogId, authorName, userName);
        rows   = new LinkedList<Row>();
        posted = new LinkedList<Row>();
        parseText(new File(inputFile)); 
        try { 
            postIndividually(blog); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        if (postedFile != null) postSummary(postedFile);
        if (notPostedFile != null) notPostedSummary(notPostedFile);
    }
    
    public static void main(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, 
            java.io.FileNotFoundException, MalformedURLException, ParseException {
        new FromText().run(args);
    }
}

