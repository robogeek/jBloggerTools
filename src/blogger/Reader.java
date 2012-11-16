
package blogger;


import com.google.gdata.client.GoogleService;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class Reader {
    
    static String encoded(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
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
        String token = getAuthToken(userName, userPasswd);
        
        // https://www.google.com/reader/api/0/tag/list?output=json&ck=116900000&client=gooberitis
        
        String sUrl = "https://www.google.com/reader/api/0/tag/list?output=json";
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        JSONObject jso = new JSONObject(json);
        
        JSONArray tags = jso.getJSONArray("tags");
        for (int i = 0; i < tags.length(); i++) {
            JSONObject elem = tags.getJSONObject(i);
            //System.out.println(elem.toString());
            System.out.println(elem.getString("id"));
        }
    }
    
    public static void feedFinder(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        String userName   = args[1];
        String userPasswd = args[2];
        String query      = args[3];
        String token = getAuthToken(userName, userPasswd);
        
        // https://www.google.com/reader/api/0/tag/list?output=json&ck=116900000&client=gooberitis
        
        String sUrl = "https://www.google.com/reader/api/0/feed-finder?output=json&q=" + encoded(query);
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        JSONObject jso = new JSONObject(json);
        System.out.println(jso.toString(4));
        
//        JSONArray tags = jso.getJSONArray("tags");
//        for (int i = 0; i < tags.length(); i++) {
//            JSONObject elem = tags.getJSONObject(i);
//            //System.out.println(elem.toString());
//            System.out.println(elem.getString("id"));
//        }
    }
    
    public static void tagEntries(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        
        // http://www.google.com/reader/api/0/stream/contents/user/06431169646684139352/label/Tech News?ck=116900000&client=gooberitis
        
        String userName   = args[1];
        String userPasswd = args[2];
        String tagName    = args[3];
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        // URL encode this string rather than just concatenate
        long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/user/-/label/" + tagName.replace(" ", "%20")
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json));
        
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
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        // URL encode this string rather than just concatenate
        long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/" + encoded("feed/"+ feed)
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json));
        
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
        String token = getAuthToken(userName, userPasswd);
        //System.out.println(token);
        
        // URL encode this string rather than just concatenate
        long now = System.currentTimeMillis();
        String sNow = Long.toString(now);
        long then = now - (3600 * 1000 * 24 * 7); // should be 7 days
        String sThen = Long.toString(then);
        String sUrl = "https://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/starred" 
            /*+ "?ck=" + sNow + "&nt="+ sThen +"&ot="+ sThen
            +"&n=400&co=true&c=CKL2t6j30KICSPTQ3f2hhawC&client=exampleCo-exampleApp-1"*/;
        URL url = new URL(sUrl);
        String json = dataForUrl(url, token);
        
        printFeed2Text(new JSONObject(json));
        
    }
    
    static void printFeed2Text(JSONObject feed)  
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException,
            org.json.JSONException
    {
        
        JSONArray items = feed.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            //System.out.println(elem.toString());
            //System.out.println(elem.getString("id"));
            System.out.println("");
            //System.out.println(item.toString(4));
            //System.out.println("");
            System.out.println("title: " + item.getString("title"));
            
            JSONObject summary =  null;
            try { summary = item.getJSONObject("summary"); } catch (Exception e) { summary = null; }
            if (summary != null)
                try { System.out.println("description: " + Feed2Text.removeNewLines(summary.getString("content"))); } catch (Exception e) { }
            
            JSONObject content = null;
            try { content = item.getJSONObject("content"); } catch (Exception e) { content = null; }
            if (content != null) 
                try { System.out.println("description: " + Feed2Text.removeNewLines(content.getString("content"))); } catch (Exception e) { }
            
            JSONObject origin = null;
            try { origin = item.getJSONObject("origin"); } catch (Exception e) { origin = null; }
            if (origin != null) 
                try {
                    System.out.println("originUrl: " + origin.getString("htmlUrl"));
                    System.out.println("originTitle: " + origin.getString("title"));
                    System.out.println("originStreamId: " + origin.getString("streamId"));
                } catch (Exception e) { }
            
            JSONArray alternate = null;
            try { alternate = item.getJSONArray("alternate"); } catch (Exception e) { alternate = null; }
            for (int altno = 0; alternate != null && altno < alternate.length(); altno++) {
                try {
                    JSONObject alt = alternate.getJSONObject(altno);
                    System.out.println("url: " + alt.getString("href"));
                } catch (Exception e) { }
            }
            
            JSONArray canonical = null;
            try { canonical = item.getJSONArray("canonical"); } catch (Exception e) { canonical = null; }
            for (int canno = 0; canonical != null && canno < canonical.length(); canno++) {
                try {
                    JSONObject can = canonical.getJSONObject(canno);
                    System.out.println("url: " + can.getString("href"));
                } catch (Exception e) { }
            }
                
            JSONArray categories = item.getJSONArray("categories");
            for (int catno = 0; catno < categories.length(); catno++) {
                String cat = categories.getString(catno);
                if (cat.matches(".*.state.com.google.*")) continue;
                if (cat.matches(".*user.*[0-9]*.label.*")) continue;
                System.out.println("tag: " + cat);
            }
            
            JSONArray enclosures = null;
            try { enclosures = item.getJSONArray("enclosure"); } catch (Exception e) { enclosures = null; }
            for (int enclno = 0; enclosures != null && enclno < enclosures.length(); enclno++) {
                try {
                    JSONObject encl = enclosures.getJSONObject(enclno);
                    System.out.println("enclosureUrl: " + encl.getString("href"));
                    System.out.println("enclusureMIME: " + encl.getString("type"));
                } catch (Exception e) { }
            }
            
            System.out.println("date: " + Long.toString(item.getLong("published")));
            System.out.println("");
        }
    }
    
    
    // Google Reader API Listing feed from a single tag or category
    // http://stackoverflow.com/questions/9819233/google-reader-api-listing-feed-from-a-single-tag-or-category
    // 
    // https://www.google.com/reader/api/0/subscription/list
    
    
}
