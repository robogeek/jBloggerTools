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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

// TODO - Separate the Row class, and parseText into it's own class, enabling other uses of parsing the data file

public class FromText {

    /**
     * Wraps thumbnail information retrieved from feeds, such as youtube query results.
     **/
    public class Thumbnail {
        public Thumbnail() {
            this.url    = null;
            this.width  = -1;
            this.height = -1;
        }
        
        public Thumbnail(String val) {
            String splits[] = val.split(" ");
            for (int i = 0; i < splits.length; i++) {
                if (splits[i].length() > 0 && splits[i].startsWith("url=")) {
                    this.url = splits[i].substring(4);
                }
                if (splits[i].length() > 0 && splits[i].startsWith("width=")) {
                    this.width = Integer.parseInt(splits[i].substring(6));
                }
                if (splits[i].length() > 0 && splits[i].startsWith("height=")) {
                    this.height = Integer.parseInt(splits[i].substring(7));
                }
            }
        }
        
        String url;
        int    width;
        int    height;

        @Override
        public String toString() {
            return " url="+ url +" width="+ Integer.toString(width) +" height="+ Integer.toString(height) +" ";
        }
    }
        
    /**
     * Represents a row of a text file.
     **/
    public class Row {
        
        public Row() {
            this.categories       = new LinkedList<String>();
            this.title            = null;
            this.date             = null;
            this.urls             = new LinkedList<String>();
            this.links            = new LinkedList<String>();
            this.uri              = null;
            this.id               = null;
            this.descriptions     = new LinkedList<String>();
            this.images           = new LinkedList<String>();
            this.youtubeUrl       = null;
            this.vimeoUrl         = null;
            this.bliptvUrl        = null;
            this.enclosureUrl     = null;
            this.enclusureMIME    = null;
            this.mediaCredit      = null;
            this.mediaDescription = null;
            this.mediaTitle       = null;
            this.thumbs           = new LinkedList<Thumbnail>();
        }
        LinkedList<String> categories;
        String title;
        LinkedList<String> urls;
        LinkedList<String> links;
        String uri;
        String id;
        String date;
        LinkedList<String> descriptions;
        LinkedList<String> images;
        String youtubeUrl;
        String vimeoUrl;
        String bliptvUrl;
        String enclosureUrl;
        String enclusureMIME;
        String mediaCredit;
        String mediaDescription;
        String mediaTitle;
        LinkedList<Thumbnail> thumbs;
        
        public boolean isEmpty() {
            if (title == null || title.length() <= 0
             /*|| url   == null || url.length() <= 0 */
             /*|| description == null || description.length() <= 0*/) {
                return true;
            } else {
                return false;
            }
        }
        
        public void addUniqueThumbnail(Thumbnail thumb) {
            boolean inThumbs = false;
            for (Thumbnail t : thumbs) {
                if (t != null && t.url.equals(thumb.url))
                    inThumbs = true;
            }
            if (! inThumbs) thumbs.add(thumb);
        }
        
        public void addUniqueImage(String imgurl) {
            if (! images.contains(imgurl))
                images.add(imgurl);
        }
        
        public void addUniqueLink(String href) {
            if (! links.contains(href))
                links.add(href);
        }
        
        public boolean hasLink(String href) {
            return links.contains(href);
        }
        
        public void addUniqueCategory(String name) {
            name = name.replace(" & ", " and ");
            if (! categories.contains(name))
                categories.add(name);
        }
        
        public void removeCategories() {
            this.categories = new LinkedList<String>();
        }
        
        public void addDescription(String desc) {
            descriptions.add(Utils.cleanup(desc));
        }
        
        public void setTitle(String title) {
            this.title = Utils.cleanup(title);
        }
        
        public void addUrl(String url) {
            urls.add(url);
        }
        
        public boolean hasUrl(String href) {
            return urls.contains(href);
        }
        
        public void removeDate() {
            this.date = "";
        }

        private static final String tmpl =
            "title: @title@\n"
           +"uri: @uri@\n"
           +"id: @id@\n"
           +"date: @date@\n"
           +"youtubeUrl: @youtubeUrl@\n"
           +"vimeoUrl: @vimeoUrl@\n"
           +"bliptvUrl: @bliptvUrl@\n"
           +"enclosureUrl: @enclosureUrl@\n"
           +"enclusureMIME: @enclusureMIME@\n"
           +"mediaCredit: @mediaCredit@\n"
           +"mediaDescription: @mediaDescription@\n"
           +"mediaTitle: @mediaTitle@\n"
           +"@urls@@links@@descriptions@@images@@thumbs@@tags@";
        private static final String tagTmpl    = "tag: @tag@\n";
        private static final String urlTmpl    = "url: @url@\n";
        private static final String linkTmpl   = "link: @link@\n";
        private static final String imgTmpl    = "image: @imageurl@\n";
        private static final String descTmpl   = "description: @description@\n";
        private static final String thumbsTmpl = "mediaThumbnail: @mediaThumbnail@\n";
        
        @Override
        public String toString() {
            String tags = "";
            for (String tag : categories) {
                tags += tagTmpl.replaceAll("@tag@", Matcher.quoteReplacement(tag));
            }
            String Slinks = "";
            for (String link : links) {
                Slinks += linkTmpl.replaceAll("@link@", Matcher.quoteReplacement(link));
            }
            String Simages = "";
            for (String image : images) {
                Simages += imgTmpl.replaceAll("@imageurl@", Matcher.quoteReplacement(image));
            }
            String thumbnails = "";
            for (Thumbnail thumb : thumbs) {
                thumbnails += thumbsTmpl.replaceAll("@mediaThumbnail@", Matcher.quoteReplacement(thumb.toString()));
            }
            String Surls = "";
            for (String Surl : urls) {
                Surls += urlTmpl.replaceAll("@url@", Matcher.quoteReplacement(Surl));
            }
            String Sdescs = "";
            for (String Sdesc : descriptions) {
                Sdescs += descTmpl.replaceAll("@description@", Matcher.quoteReplacement(Sdesc));
            }
            return tmpl
                    .replaceAll("@title@",         (title != null) ? Matcher.quoteReplacement(title) : "")
                    .replaceAll("@urls@",          Matcher.quoteReplacement(Surls))
                    .replaceAll("@links@",         Matcher.quoteReplacement(Slinks))
                    .replaceAll("@uri@",           (uri != null) ? Matcher.quoteReplacement(uri) : "")
                    .replaceAll("@id@",            (id != null) ? Matcher.quoteReplacement(id) : "")
                    .replaceAll("@date@",          (date != null) ? Matcher.quoteReplacement(date) : "")
                    .replaceAll("@images@",        Matcher.quoteReplacement(Simages))
                    .replaceAll("@descriptions@",  Matcher.quoteReplacement(Sdescs))
                    .replaceAll("@youtubeUrl@",    (youtubeUrl != null) ? Matcher.quoteReplacement(youtubeUrl) : "")
                    .replaceAll("@vimeoUrl@",      (vimeoUrl != null) ? Matcher.quoteReplacement(vimeoUrl) : "")
                    .replaceAll("@bliptvUrl@",     (bliptvUrl != null) ? Matcher.quoteReplacement(bliptvUrl) : "")
                    .replaceAll("@enclosureUrl@",  (enclosureUrl != null) ? Matcher.quoteReplacement(enclosureUrl) : "")
                    .replaceAll("@enclusureMIME@", (enclusureMIME != null) ? Matcher.quoteReplacement(enclusureMIME) : "")
                    .replaceAll("@mediaCredit@",   (mediaCredit != null) ? Matcher.quoteReplacement(mediaCredit) : "")
                    .replaceAll("@mediaDescription@", (mediaDescription != null) ? Matcher.quoteReplacement(mediaDescription) : "")
                    .replaceAll("@mediaTitle@",    (mediaTitle != null) ? Matcher.quoteReplacement(mediaTitle) : "")
                    .replaceAll("@thumbs@", Matcher.quoteReplacement(thumbnails))
                    .replaceAll("@tags@",   Matcher.quoteReplacement(tags));
        }
    }
    
    LinkedList<Row> rows   = null;
    LinkedList<Row> posted = null;
    
    public FromText() {
        rows   = new LinkedList<Row>();
        posted = new LinkedList<Row>();
    }
    
    /**
     * Parse a named file using the text format.  The items go into the "rows" list.
     **/
    private void parseText(File inputFile)
        throws java.io.FileNotFoundException, java.io.IOException
    {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        } catch (Exception e) {
            return;
        }
        String line;
        Row thisRow;
        for (thisRow = new Row(); (line = br.readLine()) != null; /*thisRow = new Row()*/) {
            line = line.trim();
            if (line.length() <= 0) {
                // - process previously gathered data
                // System.out.println("EMPTY LINE row=" + thisRow.toString());
                if (! thisRow.isEmpty()) {
                    // System.out.println("ADDED " + thisRow.uri +" "+ thisRow.title);
                    rows.addLast(thisRow);
                    // System.out.println("ADDED " + thisRow.toString());
                    thisRow = new Row();
                }
                continue;
            }
            // System.out.println(line);
            int colon = line.indexOf(":");
            if (colon > 0) {
                String name = line.substring(0, colon);
                String val  = line.substring(colon+1).trim();
//                if (name.equals("authorName"))     { authorName             = val; }
//                if (name.equals("userName"))       { userName               = val; }
//                if (name.equals("userPasswd"))     { userPasswd             = val; }
//                if (name.equals("blogId"))         { blogId                 = val; }
                if (name.equals("tag"))              { thisRow.addUniqueCategory(val); }
                if (name.equals("title"))            { thisRow.setTitle(val);          }
                if (name.equals("url"))              { thisRow.addUrl(val);            }
                if (name.equals("link"))             { thisRow.addUniqueLink(val);     }
                if (name.equals("uri"))              { thisRow.uri              = val; }
                if (name.equals("rssguid"))          { thisRow.id               = val; }
                if (name.equals("atomid"))           { thisRow.id               = val; }
                if (name.equals("date"))             { thisRow.date             = val; }
                if (name.equals("description"))      { thisRow.addDescription(val);    }
                if (name.equals("image"))            { thisRow.addUniqueImage(val);    }
                if (name.equals("youtubeUrl"))       { thisRow.youtubeUrl       = val; }
                if (name.equals("enclosureUrl"))     { thisRow.enclosureUrl     = val; }
                if (name.equals("enclusureMIME"))    { thisRow.enclusureMIME    = val; }
                if (name.equals("mediaCredit"))      { thisRow.mediaCredit      = val; }
                if (name.equals("mediaDescription")) { thisRow.mediaDescription = val; }
                if (name.equals("mediaTitle"))       { thisRow.mediaTitle       = val; }
                if (name.equals("mediaThumbnail")) {
                    Thumbnail t = new Thumbnail(val);
                    thisRow.addUniqueThumbnail(t);
                }
            }
        }
        fr.close();
    }
    
    /**
     * Post the items in "rows" to the given blog.
     **/
    private void postIndividually(Blog blog)
        throws com.google.gdata.util.ServiceException, java.io.IOException,
            java.net.MalformedURLException, ParseException
    {
        long fakeTime = new Date().getTime() - (1000 * 60 * 120); // start fake time 120 minutes ago
        for (Row row = rows.peekLast(); row != null; row = rows.peekLast()) {
            DateTime publ;
            
//            System.out.println(row.toString());
            if (row.date != null && row.date.length() > 0) {
                String[] dates = row.date.split(" ");
                Date date = new Date(new Long(dates[0]));
                publ = new DateTime(date);
                publ.setTzShift(dates.length >= 2 ? new Integer(dates[1]) : 0);
            } else {
                publ = new DateTime(fakeTime);
                publ.setTzShift(0);
                fakeTime += 120 * 1000;  // Fake an advance of time because blogger lops off seconds
            }
            
            LinkedList<String> outUrls = new LinkedList<String>();
            String urls = "";
            for (String url : row.urls) {
                url = Utils.cleanurl(Utils.urlexpander(url));
                if (outUrls.contains(url)) continue;
                outUrls.addLast(url);
                urls += linkTemplate.replaceAll("@url@", Matcher.quoteReplacement(url))
                    .replaceAll("@linkText@", url.substring(0, url.length() > 71 ? 70 : url.length() - 1));
            }
            
            String Sdesc = "";
            for (String desc : row.descriptions) {
                Sdesc += descriptTemplate.replaceAll("@description@", Matcher.quoteReplacement(desc));
            }
            if (Sdesc == null || Sdesc.equals(""))
                Sdesc = (row.mediaDescription != null && row.mediaDescription.length() > 0)
                    ? row.mediaDescription : "";
                    
            for (String url : row.urls) {
                url = Utils.cleanurl(Utils.urlexpander(url));
                String substUrl = Utils.substituteImage(url);
                if (substUrl != null) {
                    Sdesc = logoImageTemplate
                        .replaceAll("@imgurl@", substUrl)
                        .replaceAll("@description@",
                                    (Sdesc != null && Sdesc.length() > 0)
                                    ? Matcher.quoteReplacement(Sdesc) : "");
                    break;
                }
            }
            
            String MC = 
                    (row.mediaCredit != null && row.mediaCredit.length() > 0)
                    ? mediaCreditTemplate.replaceAll("@mediaCredit@", Matcher.quoteReplacement(row.mediaCredit))
                    : null;
            
            String images = "";
            for (String imgsrc : row.images) {
                images += imageTemplate.replaceAll("@imageurl@", Matcher.quoteReplacement(imgsrc));
            }
            
            String thumbs = "";
            Thumbnail theThumb = null;
            int maxArea = -1;
            for (Thumbnail thumb : row.thumbs) {
                int area = thumb.width * thumb.height;
                if (area > maxArea) {
                    theThumb = thumb;
                    maxArea = area;
                }
            }
            if (theThumb != null)
                thumbs = thumbTemplate.replaceAll("@width@", Integer.toString(theThumb.width))
                    .replaceAll("@height@", Integer.toString(theThumb.height))
                    .replaceAll("@imageurl@", Matcher.quoteReplacement(theThumb.url));
            
            String post = postBodyTemplate
                    .replaceAll("@descriptions@", Matcher.quoteReplacement(Sdesc))
                    .replaceAll("@images@",  Matcher.quoteReplacement(images))
                    .replaceAll("@youtube@", 
                        (row.youtubeUrl != null && row.youtubeUrl.length() > 0)
                        ? generateYoutubeIframe(row.youtubeUrl)
                        : ""
                    )
                    // TODO vimeoUrl
                    // TODO bliptvUrl
                    .replaceAll("@urls@", Matcher.quoteReplacement(urls))
                    .replaceAll("@thumbnails@", Matcher.quoteReplacement(thumbs))
                    .replaceAll("@mediaCredit@", MC != null ? Matcher.quoteReplacement(MC) : "")
                    .replaceAll("@enclosures@", 
                        (row.enclosureUrl != null && row.enclosureUrl.length() > 0)
                        ? generateEnclosurePlayer(row.enclosureUrl, row.enclusureMIME)
                        : ""
                    );
            IEntry Ipost = blog.createPost(Utils.cleanup(row.title),
                    post,
                    row.categories,
                    false, publ, publ);
            
            rows.remove(row);
            posted.addLast(row);
        }
    }
    
    static final String postBodyTemplate =
            "@descriptions@\n"
           +"@images@\n"
           +"@youtube@\n"
           +"@enclosures@\n"
           +"@urls@\n"
           +"@thumbnails@\n"
           +"@mediaCredit@\n";
    
    static final String postTemplate =
            "<div style='font-size: 175%; font-weight: bold; font-variant: small-caps; '>@title@</div>\n"
           +"@descriptions@\n"
           +"@images@\n"
           +"@youtube@\n"
           +"@urls@\n"
           +"@enclosures@\n"
           +"@mediaCredit@\n"
           +"<br/><br/>\n";
    
//    static final String postTemplate2 =
//            "<p><b>@title@</b></p>\n"
//           +"<p>@description@</p>\n"
//           +"@images@\n"
//           +"@youtube@\n"
//           +"@enclosures@\n"
//           +"<p><a href=\"@url@\">@url@</a></p>\n"
//           +"@mediaCredit@\n"
//           +"<br/>\n";
    
    static final String descriptTemplate = "<p>@description@</p>\n";
    static final String linkTemplate  = "<p><a href=\"@url@\">@linkText@</a></p>\n";
    static final String smLinkTemplate  = "<p><span style='font-size: small;'><a href=\"@url@\">@linkText@</a></span></p>\n";
    static final String thumbTemplate = "<p><img style='max-width: 200px' width='@width' height='@height' src=\"@imageurl@\"/></p>";
    static final String mediaCreditTemplate = "<p><b>Credit</b>: @mediaCredit@</p>\n";
    
    static final String imageTemplate = "<p><img style='max-width: 100%' src='@imageurl@'/></p>";
    static final String logoImageTemplate = "<img src='@imgurl@' align='right' style='max-width: 300px;'/>@description@";
    
    
    static final String youtubeTemplate = "<iframe width='450' height='253' src='http://www.youtube.com/embed/@code@' frameborder='0' allowfullscreen></iframe>";
    
    private void postSummary(String postedFile, String tagColor)
        throws java.net.MalformedURLException, FileNotFoundException, java.io.IOException
    {
        PrintStream out = new PrintStream(postedFile);
        String lastUniqTag = null;
        // for (Row row : posted) {
        for (Row row = posted.peekLast(); row != null; row = posted.peekLast()) {
            posted.remove(row);
            String images = "";
            for (String imgsrc : row.images) {
                images += imageTemplate.replaceAll("@imageurl@", Matcher.quoteReplacement(imgsrc));
            }
            
            String description = (row.descriptions != null && row.descriptions.size() > 0)
                    ? Utils.smallifyDescription(row.descriptions.get(0)) : null;
            if (description == null)
                description = (row.mediaDescription != null && row.mediaDescription.length() > 0)
                    ? Utils.smallifyDescription(row.mediaDescription) : null;
            
            String MC = 
                    (row.mediaCredit != null && row.mediaCredit.length() > 0)
                    ? mediaCreditTemplate.replaceAll("@mediaCredit@", Matcher.quoteReplacement(row.mediaCredit))
                    : null;
            
            LinkedList<String> outUrls = new LinkedList<String>();
            String urls = "";
            for (String url : row.urls) {
                url = Utils.cleanurl(Utils.urlexpander(url));
                if (outUrls.contains(url)) continue;
                outUrls.addLast(url);
                urls += smLinkTemplate.replaceAll("@url@", Matcher.quoteReplacement(url))
                    .replaceAll("@linkText@", url.substring(0, url.length() > 61 ? 60 : url.length() - 1));
            }
            
            for (String url : row.urls) {
                url = Utils.cleanurl(Utils.urlexpander(url));
                String substUrl = Utils.substituteImage(url);
                if (substUrl != null) {
                    description = logoImageTemplate
                        .replaceAll("@imgurl@", substUrl)
                        .replaceAll("@description@",
                                    (description != null && description.length() > 0)
                                    ? Matcher.quoteReplacement(description) : "");
                    break;
                }
            }
            
            // Colorize the tag (if any) in the title
            // But colorize it only the first time the tag is used
            String rowTitle = Utils.cleanup(row.title);
            String reMatchTag = "^([A-Za-z0-9\\s]+) - ";
            if (tagColor != null) {
                Pattern p = Pattern.compile(reMatchTag);
                Matcher m = p.matcher(rowTitle);
                if (m.find()) {
                    String tag = m.group(1);
                    if (lastUniqTag == null || ! lastUniqTag.equals(tag)) {
                        lastUniqTag = tag;
                        String csstag = "<span style='color: "+tagColor+";'>"+ tag +"</span>";
                        rowTitle = rowTitle.replaceAll(tag, csstag);
                    }
                }
            }
            
            out.println("");
            out.println(
                    postTemplate.replaceAll("@title@", Matcher.quoteReplacement(rowTitle))
                        .replaceAll("@descriptions@",  
                            (description != null && description.length() > 0)
                            ? Matcher.quoteReplacement(description) : "")
                        .replaceAll("@urls@", Matcher.quoteReplacement(urls))
                        .replaceAll("@mediaCredit@", MC != null ? Matcher.quoteReplacement(MC) : "")
                        .replaceAll("@images@", Matcher.quoteReplacement(images))
                        .replaceAll("@youtube@", 
                            (row.youtubeUrl != null && row.youtubeUrl.length() > 0)
                            ? Matcher.quoteReplacement(generateYoutubeIframe(row.youtubeUrl))
                            : "")
                        .replaceAll("@enclosures@", 
                            (row.enclosureUrl != null && row.enclosureUrl.length() > 0)
                            ? generateEnclosureSummary(row.enclosureUrl, row.enclusureMIME)
                            : ""
                        )
                    );
            out.println("");
        }
        out.close();
    }
    
    // generally should move to an oEmbed based service because it can support many more services
    //
    // Some other examples however for Vimeo & blip.tv
    // https://github.com/vimeo/vimeo-api-examples/blob/master/oembed/php-example.php
    // http://wiki.blip.tv/index.php/HTML5_Player_API
    
    // http://www.youtube.com/watch?v=c1wCszOpeYk&feature=g-vrec
    // <iframe width="450" height="253" src="http://www.youtube.com/embed/c1wCszOpeYk" frameborder="0" allowfullscreen></iframe>
    String generateYoutubeIframe(String youtubeUrl)
        throws java.net.MalformedURLException
    {
//        System.out.println("generateYoutubeIframe " + youtubeUrl);
        URL url = new URL(youtubeUrl);
        String qry = url.getQuery();
        if (qry == null || qry.length() <= 0) {
            return "";
        }
        String[] splits = qry.split("&");
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].startsWith("v=")) {
                String code = splits[i].substring(2);
//                System.out.println("\t youtube code=" + code);
                return Matcher.quoteReplacement(
                        youtubeTemplate.replaceAll("@code@", Matcher.quoteReplacement(code))
                        );
            }
        }
        return "";
    }
    
    String mediaSummaryTemplate = "<span style='font-size: x-small;'>Enclosure: @contentType@ <a href='@fileURL@'>@fileURLtxt@</a></span>";
    
    String generateEnclosureSummary(String enclUrl, String enclType) {
        return Matcher.quoteReplacement(
            mediaSummaryTemplate
                .replaceAll("@contentType@", enclType)
                .replaceAll("@fileURL@", enclUrl)
                .replaceAll("@fileURLtxt@",
                    enclUrl.substring(0, enclUrl.length() > 61 ? 60 : enclUrl.length() - 1))
        );
    }
    
    
    // This relies on MediaElementJS -- http://mediaelementjs.com/#api
    
    String mediaTemplate = "<@tagName@ style='width:100%;height:100%;max-width:100%;'>\n"
        + "<source type='@contentType@' src='@fileURL@' />\n"
        + "\n"
        + "<object width='320' height='240' type='application/x-shockwave-flash' data='http://audio.davidherron.com/MediaElement/mediaelement-2.10.1/build/flashmediaelement.swf'>\n"
        + "    <param name='movie' value='http://audio.davidherron.com/MediaElement/mediaelement-2.10.1/build/flashmediaelement.swf' />\n"
        + "    <param name='flashvars' value='controls=true&file=@encodedFileURL@' />\n"
        + "</object>\n"
        + "</@tagName@>";
        
    String imgEnclTemplate = "<div style='max-width: 100%;'><img style='max-width: 100%' src='@imgUrl@' /></div>";

    String generateEnclosurePlayer(String enclUrl, String enclType) {
        String tagName = "";
        if (enclType.startsWith("audio")) tagName = "audio";
        if (enclType.startsWith("video")) tagName = "video";
        
        if (tagName.equals("audio") || tagName.equals("video")) {
            String encodedFileURL = Utils.encoded(enclUrl);
            return Matcher.quoteReplacement(
                mediaTemplate
                    .replaceAll("@tagName@", tagName)
                    .replaceAll("@contentType@", enclType)
                    .replaceAll("@fileURL@", enclUrl)
                    .replaceAll("@encodedFileURL@", encodedFileURL)
                +"<br/>\n"
                + "<p>"+ mediaSummaryTemplate
                    .replaceAll("@contentType@", enclType)
                    .replaceAll("@fileURL@", enclUrl) + "</p>"
            );
        }
        if (enclType.startsWith("image")) {
            return Matcher.quoteReplacement(
                imgEnclTemplate
                    .replaceAll("@imgUrl@", enclUrl)
            );
        }
        return "";
    }
    
    String authorName = null; 
    String userName   = null; 
    String userPasswd = null; 
    
    String blogId = null; 
    String inputFile = null; 
    
    String postedFile = null;
    String notPostedFile = null;
    
    String tagColor = null;
    
    void run(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, java.io.FileNotFoundException,
            java.net.MalformedURLException, ParseException, Exception {
        
        authorName = args[1];
        userName   = args[2];
        userPasswd = args[3];
        blogId     = args[4];
        inputFile  = args[5];
        
        if (args.length >= 7) postedFile    = args[6];
        if (args.length >= 8) notPostedFile = args[7];
        if (args.length >= 9 && args[8].length() > 0) tagColor = args[8];
        
        System.out.println("author name: " + authorName);
        System.out.println("user name: " + userName);
        System.out.println("user password: " + userPasswd);
        System.out.println("blog ID: " + blogId);
        System.out.println("input file: " + inputFile);
        System.out.println("posted file: " + postedFile);
        System.out.println("not posted file: " + notPostedFile);
        
        BloggerService service = new BloggerService("exampleCo-exampleApp-1");
        boolean notAuthenticated = true;
        for (int i = 0; i < 5 && notAuthenticated; i++) {
            try {
                service.setUserCredentials(userName, userPasswd);
                notAuthenticated = false; // we only get here if there's no exception
            } catch (com.google.gdata.util.AuthenticationException ae) {
                System.out.println("CAUGHT EXCEPTION AuthenticationException "+ ae.getMessage());
                System.err.println("Response body " + ae.getResponseBody());
                System.err.println("Code name " + ae.getCodeName());
                System.err.println("Debug info " + ae.getDebugInfo());
                System.err.println("Domain name " + ae.getDomainName());
                System.err.println("Extended help " + ae.getExtendedHelp());
                System.err.println("Internal reason " + ae.getInternalReason());
                System.err.println("Location " + ae.getLocation());
                System.err.println("string: " + ae.toString());
                try {
                    // The exception may be due to blogger being overloaded
                    // sleep to give time for blogger to catch up .. maybe ..?
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        }
        if (notAuthenticated) {
            throw new Exception("Blog service did not authenticate.");
        }
        Blog blog = new Blog(service, blogId, authorName, userName);
        parseText(new File(inputFile)); 
        try { 
            postIndividually(blog); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        if (postedFile != null) postSummary(postedFile, tagColor);
        if (notPostedFile != null) {
            writeRowsToFile(notPostedFile, rows);
        }
        rows.clear();
    }
    
    public void countItems(String fn) throws FileNotFoundException, IOException {
        parseText(new File(fn));
        System.out.println("input file: " + fn); 
        System.out.println("number of items: " + Integer.toString(rows.size()));
    }
    
    public void generateSummary(String[] args) 
        throws FileNotFoundException, IOException 
    {
        
        String fnIn = args[1];
        String fnOut = args[2];
        String tagColor = null;
        
        if (args.length >= 4) tagColor = args[3];
        
        parseText(new File(fnIn));
        // Simulate posting the items
        for (Row row = rows.peekLast(); row != null; row = rows.peekLast()) {
            rows.remove(row);
            posted.addLast(row);
        }
        postSummary(fnOut, tagColor);
    }
    
    public void rmDate(String[] args)
        throws FileNotFoundException, IOException 
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        
        parseText(new File(inputFile));
        
        for (Row row : rows) {
            row.removeDate();
        }
        writeRowsToFile(outputFile, rows);
    }
    
    public void rmTags(String[] args)
        throws FileNotFoundException, IOException 
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        
        parseText(new File(inputFile));
        
        for (Row row : rows) {
            row.removeCategories();
        }
        writeRowsToFile(outputFile, rows);
    }
    
    public void addTag(String[] args)
        throws FileNotFoundException, IOException 
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        String tag        = args[3];
        
        parseText(new File(inputFile));
        
        for (Row row : rows) {
            row.addUniqueCategory(tag);
        }
        writeRowsToFile(outputFile, rows);
    }
    
    public void tagTitle(String[] args)
        throws FileNotFoundException, IOException 
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        String tag        = args[3];
        
        parseText(new File(inputFile));
        
        for (Row row : rows) {
            row.setTitle(tag +" - "+ row.title);
        }
        writeRowsToFile(outputFile, rows);
    }
    
    public void sortTitle(String[] args)
        throws FileNotFoundException, IOException 
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        
        parseText(new File(inputFile));
        
        Collections.sort(rows, new Comparator<Row>() {
            public int compare(Row r1, Row r2) {
                return r1.title.compareTo(r2.title);
            }
        });
        
        writeRowsToFile(outputFile, rows);
    }
    
    public void merge(String[] args) 
        throws FileNotFoundException, IOException
    {
        String uriFile  = args[1];
        String outFile  = args[2];
        String urilist = readAll(uriFile);
        
        for (int i = 3; i < args.length; i++) {
            // System.out.println("********* " + args[i]);
            parseText(new File(args[i]));
        }
        
        LinkedList<Row> newrows   = new LinkedList<Row>();
        for (Row row : rows) {
            if (row.uri == null || row.uri.equals("") || (! urilist.contains(row.uri))) {
                newrows.addLast(row);
                // System.out.println("Adding to output: "+ row.uri +" "+ row.title);
                urilist += row.uri + "\n";
            } /* else {
                System.out.println("Skipping: "+ row.uri +" "+ row.title);
            }*/
        }
        
        writeRowsToFile(outFile, newrows);
        writeAll(urilist, uriFile);
    }
    
    public void addToUriList(String[] args)
        throws FileNotFoundException, IOException
    {
        String uriFile  = args[1];
        String txtFile  = args[2];
        
        parseText(new File(txtFile));
        String urilist = readAll(uriFile);
        for (Row row : rows) {
            if (! urilist.contains(row.uri)) {
                urilist += row.uri + "\n";
            }
        }
        writeAll(urilist, uriFile);
    }
    
    public void expungeByUri(String[] args) 
        throws FileNotFoundException, IOException
    {
        String uriFile  = args[1];
        String txtFile  = args[2];
        
        parseText(new File(txtFile));
        String urilist = readAll(uriFile);
        
        LinkedList<Row> newrows   = new LinkedList<Row>();
        for (Row row : rows) {
            if (! urilist.contains(row.uri)) {
                newrows.addLast(row);
                urilist += row.uri + "\n";
            }
        }
        
        writeRowsToFile(txtFile, newrows);
        writeAll(urilist, uriFile);
    }
    
    String readAll(String fname) 
        throws FileNotFoundException, IOException
    {
        String ret = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String line = null;
            for (line = in.readLine(); line != null; line = in.readLine()) {
                ret += line + "\n";
            }
        }
        catch (Exception e) { }
        return ret;
    }
    
    void writeAll(String urilist, String fname)
        throws IOException
    {
        BufferedWriter bout = new BufferedWriter(new FileWriter(fname));
        bout.write(urilist);
        bout.close();
    }
    
    void writeRowsToFile(String fname, LinkedList<Row> therows)
        throws FileNotFoundException
    {
        PrintStream out = new PrintStream(fname);
        for (Row row : therows) {
            out.println("");
            out.println(row.toString());
            out.println("");
        }
        out.close();
    }
    
    public void expandurls(String[] args)
        throws FileNotFoundException, java.io.IOException
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        
        parseText(new File(inputFile));
        
        for (Row row : rows) {
            LinkedList<String> newurls  = new LinkedList<String>();
            LinkedList<String> newlinks = new LinkedList<String>();
            for (String link : row.links) {
                try {
                    newlinks.addLast(Utils.urlexpander(link));
                } catch (Exception e) {
                    newlinks.addLast(link);
                }
            }
            row.links = newlinks;
            for (String url : row.urls) {
                try {
                    newurls.addLast(Utils.urlexpander(url));
                } catch (Exception e) {
                    newurls.addLast(url);
                }
            }
            row.urls = newurls;
        }
        writeRowsToFile(outputFile, rows);
    }
    
    public void dedupe(String[] args)
        throws FileNotFoundException, java.io.IOException
    {
        String inputFile  = args[1];
        String outputFile = args[2];
        
        parseText(new File(inputFile));
        
        LinkedList<Row> newrows   = new LinkedList<Row>();
        
        for (Row row : rows) {
            boolean addit = true;
            for (Row nrow : newrows) {
                for (String nlink : row.links) {
                    if (nrow.hasLink(nlink)) addit = false;
                }
                for (String url : row.urls) {
                    if (nrow.hasUrl(url)) addit = false;
                }
            }
            if (addit) newrows.addLast(row);
        }
        writeRowsToFile(outputFile, newrows);
    }
    
    public static void main(String[] args) 
            throws ParserConfigurationException, SAXException, IOException,
            AuthenticationException, ServiceException, 
            java.io.FileNotFoundException, MalformedURLException, ParseException, Exception {
        new FromText().run(args);
    }
}

