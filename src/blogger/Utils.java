package blogger;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    
    public static String smallifyDescription(String desc) {
        // Start the smallified description with no HTML tags, and really short
        String ret = cleanup(desc);
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
            ret += desc.substring(m.start(), m.end());
        }
        return ret;
    }
    
    public static String removeNewLines(String s) {
        while (s.indexOf("\n") >= 0) {
            String n = s.substring(0, s.indexOf("\n")) + s.substring(s.indexOf("\n") + 1);
            s = n;
        }
        return s;
    }
    
    static String cleanTitle(String title) {
        return title.replaceAll("<b>", "")
            .replace("</b>", "")
            .replace(new String(new byte[] { (byte)0xD1 }), "");
    }
    
    static String cleanup(String txt) {
        String ret = null;
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
            .replace(new String(new byte[] { (byte)0xc2, (byte)0xa0 }), " ");
        
        // Ensure all images fit within the bounds of the blog
        
        Pattern img = Pattern.compile("<(img[^>]*)>");
        Matcher m = img.matcher(ret);
        while (m.find()) {
            String imgtext = m.group(1);
            // Remove height= and width= from all img tags
            String imgtext2 = imgtext
                .replaceAll("width=['\"][^'\"]*['\"]", "")
                .replaceAll("height=['\"][^'\"]*['\"]", "")
                .replaceAll("  ", " ");
            // Then add style='max-width:100%' to all img tags
            if (! imgtext2.contains("style='max-width")) {
                imgtext2 += " style='max-width: 100%'";
            }
            // Then reassemble
            String retsave = ret;
            try {
                ret = ret.substring(0, m.start() >= 0 ? m.start() : 0)
                + "<" + imgtext2 + ">"
                + ret.substring(m.end());
            } catch (Exception e) {
                ret = retsave;
            }
        }
        
        return ret;
    }
    
    static String encoded(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
}