
package blogger;


import com.google.gdata.client.GoogleService;
import java.io.PrintStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONArray;

import java.text.Normalizer;

public class Reader {
    
    public static String getAuthToken(String userName, String userPasswd) {
        return ClientLogin.getAuthToken(ClientLogin.GOOGLE, userName, userPasswd, "reader", "exampleCo-exampleApp-1");
    }
    
    public static String dataForUrl(URL url, String token)
        throws java.io.IOException, java.net.MalformedURLException
    {
        //System.out.println(url.toString());
        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Authorization", "GoogleLogin auth=" + token);
        
        /*Map<String,List<String>> reqprop = connection.getRequestProperties();
        System.out.println(url.toString());
        System.out.println("GoogleLogin auth=" + token);
        System.out.println(reqprop.toString());*/
        
        InputStream istream = null;
        try {
            istream = connection.getInputStream();
        } catch (Exception e1) {
            Map<String,List<String>> headers = connection.getHeaderFields();
            System.err.println(headers.toString());
            System.err.println("ERROR " + e1.getMessage());
            e1.printStackTrace();
            return null;
        }
        InputStreamReader is = new InputStreamReader(istream);
        BufferedReader in = new BufferedReader(is);
        String data = "";
        String decodedString;
        while ((decodedString = in.readLine()) != null) {
            data += decodedString;
        }
        in.close();
        return data;
    }
    
    public static void listTags(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        String userName   = args[1];
        String userPasswd = args[2];
        String outfn      = args.length >= 4 ? args[3] : "-";
        PrintStream out = System.out;
        if (outfn != null && ! outfn.equals("-")) {
            out = new PrintStream(outfn);
        }
        String token = getAuthToken(userName, userPasswd);
        
        // https://www.google.com/reader/api/0/tag/list?output=json&ck=116900000&client=gooberitis
        
        String sUrl = "https://www.google.com/reader/api/0/tag/list?output=json";
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        JSONObject jso = new JSONObject(json);
        
        JSONArray tags = jso.getJSONArray("tags");
        for (int i = 0; i < tags.length(); i++) {
            JSONObject elem = tags.getJSONObject(i);
            //out.println(elem.toString());
            out.println(elem.getString("id"));
        }
    }
    
    public static void feedFinder(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        String userName   = args[1];
        String userPasswd = args[2];
        String query      = args[3];
        String outfn      = args.length >= 5 ? args[4] : "-";
        PrintStream out = System.out;
        if (outfn != null && ! outfn.equals("-")) {
            out = new PrintStream(outfn);
        }
        String token = getAuthToken(userName, userPasswd);
        
        String sUrl = "https://www.google.com/reader/api/0/feed-finder?output=json&q=" + Utils.encoded(query);
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        JSONObject jso = new JSONObject(json);
        out.println(jso.toString(4));
    }
    
    public static void tagEntries(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        
        // http://www.google.com/reader/api/0/stream/contents/user/06431169646684139352/label/Tech News?ck=116900000&client=gooberitis
        
        String userName   = args[1];
        String userPasswd = args[2];
        String tagName    = args[3];
        String outfn      = args.length >= 5 ? args[4] : "-";
        String hours      = args.length >= 6 ? args[5] : "0";
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        /*long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);*/
        
        int nHours = Integer.parseInt(hours);
        long oldest = nHours > 0 ? System.currentTimeMillis() - (nHours * 60 * 60 * 1000) : 0;
        //if (oldest > 0) System.out.println("oldest=" + new Date(oldest).toString());
        
        // URL encode this string rather than just concatenate
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/user/-/label/" + tagName.replace(" ", "%20")
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json), outfn, oldest);
        
        /*JSONObject jso = new JSONObject(json);
        System.out.println(jso.toString(4));*/
    }
    
    public static void feed(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        // http://www.google.com/reader/api/0/stream/contents/user/06431169646684139352/label/Tech News?ck=116900000&client=gooberitis
        
        String userName   = args[1];
        String userPasswd = args[2];
        String feed       = args[3];
        String outfn      = args.length >= 5 ? args[4] : "-";
        String hours      = args.length >= 6 ? args[5] : "0";
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        // URL encode this string rather than just concatenate
        /*long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);*/
        
        int nHours = Integer.parseInt(hours);
        long oldest = nHours > 0 ? System.currentTimeMillis() - (nHours * 60 * 60 * 1000) : 0;
        //if (oldest > 0) System.out.println("oldest=" + new Date(oldest).toString());
        
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/" + Utils.encoded("feed/"+ feed)
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json), outfn, oldest);
        
        /*JSONObject jso = new JSONObject(json);
        System.out.println(jso.toString(4));*/
    }
    
    public static void starred(String[] args) 
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        // https://www.google.com/reader/api/0/stream/contents/user/<usrId>/state/com.google/starred
        
        String userName   = args[1];
        String userPasswd = args[2];
        String outfn      = args.length >= 4 ? args[3] : "-";
        String hours      = args.length >= 5 ? args[6] : "0";
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        // URL encode this string rather than just concatenate
        /*long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);*/
        
        int nHours = Integer.parseInt(hours);
        long oldest = nHours > 0 ? System.currentTimeMillis() - (nHours * 60 * 60 * 1000) : 0;
        //if (oldest > 0) System.out.println("oldest=" + new Date(oldest).toString());
        
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/starred" 
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json), outfn, oldest);
    }
    
    static void printFeed2Text(JSONObject feed, String outfn, long oldest)  
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        JSONArray items = feed.getJSONArray("items");
        if (items.length() <= 0) {
            new PrintStream(outfn).close();
            return;
        }
        PrintStream out = System.out;
        if (outfn != null && ! outfn.equals("-")) {
            out = new PrintStream(outfn);
        }
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            
            long timestampUsec = -1;
            try { timestampUsec = item.getLong("timestampUsec"); } catch (Exception e) { timestampUsec = -1; }
            long publ = -1;
            try { publ = item.getLong("published"); } catch (Exception e) { publ = -1; }
            if (timestampUsec > 0) {
                publ = (long)(timestampUsec / 1000);
            }
            //System.out.println(new Date(publ).toString() +" "+ item.getString("title"));
            
            if (publ < oldest) {
                //System.out.println("TOO OLD - skipping");
                continue;
            }
            
            //System.out.println(elem.toString());
            //System.out.println(elem.getString("id"));
            //out.println("");
            //System.out.println(item.toString(4));
            out.println("");
            out.println("title: " + Utils.cleanTitle(Utils.cleanup(item.getString("title"))));
            
            JSONObject summary =  null;
            try { summary = item.getJSONObject("summary"); } catch (Exception e) { summary = null; }
            if (summary != null)
                try { out.println("description: " + Utils.cleanup(Utils.removeNewLines(summary.getString("content")))); } catch (Exception e) { }
            
            JSONObject content = null;
            try { content = item.getJSONObject("content"); } catch (Exception e) { content = null; }
            if (content != null) 
                try { out.println("description: " + Utils.cleanup(Utils.removeNewLines(content.getString("content")))); } catch (Exception e) { }
            
            JSONObject origin = null;
            try { origin = item.getJSONObject("origin"); } catch (Exception e) { origin = null; }
            if (origin != null) 
                try {
                    out.println("originUrl: " + origin.getString("htmlUrl"));
                    out.println("originTitle: " + origin.getString("title"));
                    out.println("originStreamId: " + origin.getString("streamId"));
                } catch (Exception e) { }
            
            JSONArray alternate = null;
            try { alternate = item.getJSONArray("alternate"); } catch (Exception e) { alternate = null; }
            for (int altno = 0; alternate != null && altno < alternate.length(); altno++) {
                try {
                    JSONObject alt = alternate.getJSONObject(altno);
                    out.println("url: " + alt.getString("href"));
                } catch (Exception e) { }
            }
            
            JSONArray canonical = null;
            try { canonical = item.getJSONArray("canonical"); } catch (Exception e) { canonical = null; }
            for (int canno = 0; canonical != null && canno < canonical.length(); canno++) {
                try {
                    JSONObject can = canonical.getJSONObject(canno);
                    out.println("url: " + can.getString("href"));
                } catch (Exception e) { }
            }
                
            JSONArray categories = item.getJSONArray("categories");
            for (int catno = 0; catno < categories.length(); catno++) {
                String cat = categories.getString(catno);
                if (cat.matches(".*.state.com.google.*")) continue;
                if (cat.matches(".*user.*[0-9]*.label.*")) continue;
                out.println("tag: " + cat);
            }
            
            JSONArray enclosures = null;
            try { enclosures = item.getJSONArray("enclosure"); } catch (Exception e) { enclosures = null; }
            for (int enclno = 0; enclosures != null && enclno < enclosures.length(); enclno++) {
                try {
                    JSONObject encl = enclosures.getJSONObject(enclno);
                    out.println("enclosureUrl: " + encl.getString("href"));
                    out.println("enclusureMIME: " + encl.getString("type"));
                } catch (Exception e) { }
            }
            
            out.println("date: " + Long.toString(publ));
            out.println("");
        }
    }
    
}
