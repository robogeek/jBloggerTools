package blogger;

import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    
    /**
     * Radically shorten text by removing all HTML tags, cutting the total length to 450 characters,
     * and leaving just one image tag.  This is meant for those sites that publish full articles
     * in RSS feeds and/or publish a bunch of excess HTML in their feed.  
     **/
    public static String smallifyDescription(String desc) {
        // Start the smallified description with no HTML tags, and really short
        String ret = cleanup(desc);
        // Get rid of any tag, period
        ret = ret.replaceAll("<[^>]*>", "");
        String retsave = ret;
        try { ret = ret.substring(0, ret.length() > 451 ? 450 : ret.length() - 1); } catch (Exception e) {
            ret = retsave;
        }
        ret += " ...";
        
        // Then find the first image in desc, and append that to the returned string
        Pattern p = Pattern.compile("<img[^>]*>");
        Matcher m = p.matcher(desc);
        if (m.find()) {
            ret += "<div style='clear:both'>" + desc.substring(m.start(), m.end()) + "</div>";
        }
        return ret;
    }
    
    /**
     * Remove all the newline characters from strings.
     **/
    public static String removeNewLines(String s) {
        while (s.indexOf("\n") >= 0) {
            String n = s.substring(0, s.indexOf("\n")) + s.substring(s.indexOf("\n") + 1);
            s = n;
        }
        return s;
    }
    
    /**
     * Remove excess junk from titles that make titles render badly.
     **/
    static String cleanTitle(String title) {
        return title.replaceAll("<b>", "")
            .replace("</b>", "")
            .replace(new String(new byte[] { (byte)0xD1 }), "");
    }
    
    /**
     * General cleanup of article text so that it renders nicely.  Removes all 1x1 images.
     * Removes unicode or UTF-8 sequences that render badly.  Remove some links like the ones
     * feedburner inserts for sharing by email or whatnot.  Remove certain empty tags.
     * Change all images to remove height= and width= replacing it with a max-width=100% so
     * that images automatically fit into the blog.
     **/
    static String cleanup(String txt) {
        String ret = null;
        // System.out.println("cleanup: " + txt);
        ret = txt.replaceAll("<iframe[^>]*>[^<]*</iframe>", "")
            // Get rid of all 1x1 images
            .replaceAll("<img[^h>]*height=['\"]1['\"][^w]*width=['\"]1['\"][^<]*</img>", "")
            .replaceAll("<img[^w>]*width=['\"]1['\"][^h]*height=['\"]1['\"][^<]*</img>", "")
            .replaceAll("<img[^h>]*height=['\"]1['\"][^w>]*width=['\"]1['\"][^>]*>", "")
            .replaceAll("<img[^w>]*width=['\"]1['\"][^h>]*height=['\"]1['\"][^>]*>", "")
            // Get rid of stuff from doubleclick
            .replaceAll("<a[^h>]*href=['\"]http://googleads.g.doubleclick.net[^>]*>[^<]*</a>", "")
            // Get rid of stuff from Wordpress
            .replaceAll("<img[^>s]+src\\s*=\\s*['\"]http...stats.wordpress.com[^>]*>", "")
            // Get rid of stuff from feedsportal
            .replaceAll("<img[^>s]+src\\s*=\\s*['\"]http...res3.feedsportal.com[^>]*>", "")
            .replaceAll("<img[^>s]+src\\s*=\\s*['\"]http...da.feedsportal.com[^>]*>", "")
            .replaceAll("<a[^h>]*href=['\"]http://da.feedsportal.com/[^>]*>[^<]*</a>", "")
            .replaceAll("<a[^h>]*href=['\"]http://share.feedsportal.com/[^>]*>[^<]*</a>", "")
            .replaceAll("<a[^h>]*href=['\"]http://res.feedsportal.com/[^>]*>[^<]*</a>", "")
            // Get rid of stuff from feedburner
            .replaceAll("<img[^>s]+src\\s*=\\s*['\"]http://feeds.feedburner.com[^>]*>", "")
            .replaceAll("<a[^h>]*href=['\"]http://feeds.feedburner.com..ff[^>]*>[^<]*</a>", "")
            .replaceAll("<a[^h>]*href=['\"]http://feeds.importantmedia.org..ff[^>]*>[^<]*</a>", "")
            // Get rid of empty table stuff 
            .replaceAll("<td[^>]*>\\s*</td>", "")
            .replaceAll("<tr[^>]*>\\s*</tr>", "")
            .replaceAll("<table[^>]*>\\s*</table>", "")
            .replaceAll("<div[^>]*>\\s*</div>", "")
            .replaceAll("<span[^>]*>\\s*</span>", "")
            // Convert unicode and UTF-8 cruft to useful characters
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x93 }), "-")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x94 }), "-")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x95 }), "-")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x98 }), "'")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x99 }), "'")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x9a }), ",")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x9c }), "\"")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x9d }), "\"")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0x9e }), "\"")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0xa6 }), "...")
            .replace(new String(new byte[] { (byte)0xe2, (byte)0x80, (byte)0xb2 }), "'")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x80 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x81 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x82 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x83 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x84 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x85 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x86 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x87 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x88 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x89 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x90 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x91 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x92 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x93 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x94 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x95 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x96 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x97 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x98 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0x99 }), "")
            .replace(new String(new byte[] { (byte)0xc2, (byte)0xa0 }), " ")
            .replace(new String(new byte[] { (byte)0x89, (byte)0x3f }), " ")
            .replace(new String(new byte[] { (byte)0xcc, (byte)0x3f }), " ")
            .replace(new String(new byte[] { (byte)0xca, (byte)0x72 }), " ")
            .replace(new String(new byte[] { (byte)0xd5, (byte)0x73 }), " ");
        
        // Ensure all images fit within the bounds of the blog
        
        Pattern img = Pattern.compile("<(img[^>]*)>");
        // System.out.println("cleanup: " + ret);
        Matcher m = img.matcher(ret);
        while (m.find()) {
            String imgtext = m.group(1);
            // System.out.println("BEFORE: " + imgtext);
            if (imgtext.endsWith("/")) {
                String nImgText = imgtext.substring(0, imgtext.length() - 1);
                imgtext = nImgText;
            }
            // Remove height= and width= from all img tags
            String imgtext2 = imgtext
                .replaceAll("width=['\"][^'\"]*['\"]", "")
                .replaceAll("height=['\"][^'\"]*['\"]", "")
                /*.replaceAll("  ", " ")*/;
            // Then add style='max-width:100%' to all img tags
            if (! imgtext2.contains("style")) { //='max-width")) {
                imgtext2 += " style='max-width: 100%; clear: both;'";
            }
            ret = ret.replace(imgtext, imgtext2);
            // System.out.println("AFTER: " + imgtext2);
        }
        
        return ret;
    }
    
    static String urlexpander(String url) 
        throws java.net.MalformedURLException, java.io.IOException
    {
        return urlexpander(new URL(url));
    }
    
    static String urlexpander(URL url)
        throws java.net.MalformedURLException, java.io.IOException
    {
        // The idea here is that with .setFollowRedirects(true), Java will go ahead
        // and traverse the redirects from the short URL service(s)
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        HttpURLConnection.setFollowRedirects(true);
        c.setInstanceFollowRedirects(true);
        c.setConnectTimeout(15000);
        c.setReadTimeout(15000);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
        c.connect();
        // Once we do this ...
        c.getInputStream();
        // ... .getURL() returns the right URL
        return c.getURL().toString();
    }
    
    static String cleanurl(String url) 
        throws java.net.MalformedURLException
    {
        return cleanurl(new URL(url));
    }
    
    /**
     * Clean up the Internet by removing unneeded cruft from URL's, which in turn
     * will let us remove excess URL's from postings created by jBloggerTools.
     * The idea is that feedburner and that ilk are adding extra things as query arguments
     * but which are unnecessary to preserve.  In particular, the process of removing duplicate
     * URL's from a posting is getting tripped up by these extra arguments.  
     **/
    static String cleanurl(URL url)
        throws java.net.MalformedURLException
    {
        String qry = url.getQuery();
        if (qry == null || qry.length() <= 0) return url.toString();
        
        // Split the query string by '&' to get at the individual arguments
        // Any argument we don't like does not get added to the newQry string
        String[] args = qry.split("&");
        String newQry = "";
        for (int i = 0; i < args.length; i++) {
            // List here the query arguments to remove from URL's
            if (args[i].startsWith("utm_source")
             || args[i].startsWith("utm_medium")
             || args[i].startsWith("utm_campaign")
                ) {
                continue;
            }
            // else
            // Add the argument string to newQry
            // Note that at no time did we URLDecode the strings
            // Hence it is safe to just glue them back together this way
            if (newQry.length() <= 0) {
                newQry += args[i];
            } else {
                newQry += "&" + args[i];
            }
        }
        
        // Get a query-less version of the input URL
        URL newUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
        String sNewUrl = newUrl.toString();
        // If there is a new query string, append it
        if (newQry.length() > 0) sNewUrl += "?" + newQry;
        return sNewUrl;
    }
    
    /**
     * Run text through URLEncoder
     **/
    static String encoded(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
}
