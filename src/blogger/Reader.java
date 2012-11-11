
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

public class Reader {
    
    public static void listTags(String[] args)
        throws java.net.MalformedURLException, java.io.IOException, com.google.gdata.util.AuthenticationException
    {
        String userName   = args[1];
        String userPasswd = args[2];
        
        
        String token = ClientLogin.getAuthToken(userName, userPasswd, "reader", "exampleCo-exampleApp-1");
        
        // https://www.google.com/reader/api/0/tag/list?output=json&ck=116900000&client=gooberitis
        
        String sUrl = "https://www.google.com/reader/api/0/tag/list?output=json&ck="
                + System.currentTimeMillis()  + "&client=exampleCo-exampleApp-1";
        URL url = new URL(sUrl);
        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Authorization", "GoogleLogin auth=" + token);
        
        // Map<String,List<String>> props = connection.getRequestProperties();
        // System.err.println(props.toString());
        
        InputStream istream = null;
        try {
            istream = connection.getInputStream();
        } catch (Exception e1) {
            Map<String,List<String>> headers = connection.getHeaderFields();
            System.err.println(headers.toString());
            System.err.println("ERROR " + e1.getMessage());
            e1.printStackTrace();
            return;
        }
        InputStreamReader is = new InputStreamReader(istream);
        BufferedReader in = new BufferedReader(is);
        String decodedString;
        while ((decodedString = in.readLine()) != null) {
            System.out.println(decodedString);
        }
        in.close();
    }
    
    public static void tagEntries(String[] args) {
        
    }
}
