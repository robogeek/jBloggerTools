package blogger;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class Utils {
    
    public static String removeNewLines(String s) {
        while (s.indexOf("\n") >= 0) {
            String n = s.substring(0, s.indexOf("\n")) + s.substring(s.indexOf("\n") + 1);
            s = n;
        }
        return s;
    }
    
    static String cleanTitle(String title) {
        String ret = null;
        ret = title.replaceAll("<b>", "").replace("</b>", "");
        return ret;
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