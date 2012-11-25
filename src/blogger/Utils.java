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
    
    static String cleanup(String txt) {
        String ret = null;
        ret = txt.replaceAll("<iframe[^>]*>[^<]*</iframe>", "")
            .replace("<img>[^h<]*height=['\"]1['\"][^w<]*width=['\"]1['\"][^<]*</img>", "")
            .replace("<img>[^w<]*width=['\"]1['\"][^h<]*height=['\"]1['\"][^<]*</img>", "")
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