//
// This file is part of the INGInious BlueJ plugin.
//
// The INGInious BlueJ plugin is free software: you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either
// version 3 of the License, or (at your option) any later version.
//
// The INGInious BlueJ plugin is distributed in the hope that it will
// be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with the INGInious BlueJ plugin.  If not, see <http://www.gnu.org/licenses/>.
//

package inginious.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * Main class for using the INGInious API
 */
public class API {
    private String sessionId;
    private String username;
    private String apiUrl;
    private Gson gson;

    /**
     * Initialize a new API object from an INGInious API url
     * @param apiUrl Path to INGInious API
     */
    public API(String apiUrl) {
        this.apiUrl = apiUrl;
        gson = new GsonBuilder().create();
    }

    /**
     * Initialize a new API object from an INGInious API url and session id
     * @param apiUrl Path to INGInious API
     * @param sessionId Session ID as returned by the cookies
     */
    public API(String apiUrl, String sessionId) {
        this(apiUrl);
        this.sessionId = sessionId;
    }

    /**
     * Returns the username of the current logged user
     * @return Username of the logged user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the path to the INGInious API
     * @param url Path to INGInious API 
     */
    public void setUrl(String url) {
        this.apiUrl = url;
    }

    /**
     * Returns the path to the INGInious API
     * @return Path to the INGinious API
     */
    public String getUrl() {
        return this.apiUrl;
    }

    /**
     * Returns the session ID of the current user
     * @return Possibly empty session ID string
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Returns the Gson instance
     * @return Gson object
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Indicates if the current user is authenticated on the server
     * @return True if authenticated, else False
     * @throws Exception
     */
    public boolean isAuthenticated() throws Exception {
        HttpURLConnection conn = getHttpURLConnection("authentication");
        InternalAuthStatus status = gson.fromJson(readHTTPContent(conn), InternalAuthStatus.class);
        conn.disconnect();
        username = status.username;
        return status.authenticated;
    }

    /**
     * Authenticate current user on the server
     * @param authMethod Authentication method chosen with value-set Authentication inputs
     * @return True if authenticated, else False
     * @throws Exception
     */
    public boolean authenticate(AuthMethod authMethod) throws Exception {
        HttpURLConnection conn = getHttpURLConnection("authentication");

        // Prepare post parameters
        String params = "auth_method_id=" + authMethod.getId();
        for(AuthInput input : authMethod.getInput())
            params += "&" + input.getId() + "=" + URLEncoder.encode(input.getValue(), "UTF-8");

        // Post data
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();

        // Take decision based on the response code
        boolean authenticated = false;
        switch(conn.getResponseCode())
        {
        case 200:
            // Store sessionId
            sessionId = parseCookies(conn).get("webpy_session_id");

            // Normally, login is accepted, but we'll check is received message is correct  
            Map<String, String> map = gson.fromJson(readHTTPContent(conn), new TypeToken<Map<String,String>>(){}.getType());

            authenticated = map.get("status").equals("success");     
            username = map.get("username");
        case 403:
        default:
        }

        conn.disconnect();

        return authenticated;
    }

    /**
     * Parse the cookies returned by the server to a String to String map
     * @param conn Opened HttpURLConnection connection
     * @return Possibly empty HashMap with cookies name as keys
     */
    private Map<String,String> parseCookies(HttpURLConnection conn) {
        Map<String,String> cookies = new HashMap<String,String>();

        // Multiples cookies can be set in a single request, parse them all
        String headerName=null;
        for (int i=1; (headerName = conn.getHeaderFieldKey(i))!=null; i++) {
            if (headerName.equals("Set-Cookie")) {

                // Parse each line of Set-Cookie
                for(String cookieToken : conn.getHeaderField(i).split(";"))
                {
                    String[] tokenParts = cookieToken.split("=");
                    if(tokenParts.length > 1)
                        cookies.put(tokenParts[0].trim(), tokenParts[1].trim());
                    else
                        cookies.put(tokenParts[0].trim(), "");
                }
            }
        }

        return cookies;
    }

    /**
     * Try to fetch the API content at the specified URL and returns an exception if not
     * @param myApiUrl Path to the INGInious API to be tested
     * @throws Exception
     */
    public void checkConnection(String myApiUrl) throws Exception {
        HttpURLConnection conn = getHttpURLConnection(myApiUrl, "auth_methods");
        conn.getContent();
    }

    /**
     * Returns a new HttpURLConnection to the specified subpath of the INGInious API
     * @param spec Subpath to the INGInious API
     * @return HttpURLConnection
     * @throws Exception
     */
    HttpURLConnection getHttpURLConnection(String spec) throws Exception {
        return getHttpURLConnection(apiUrl, spec);
    }

    /**
     * Returns a new HttpURLConnection to the specified path and subpath of the INGInious API
     * @param myApiUrl Path to the INGInious API
     * @param spec Subpath to the INGInious API
     * @return HttpURLConnection
     * @throws Exception
     */
    HttpURLConnection getHttpURLConnection(String myApiUrl, String spec) throws Exception {
        URL url = new URL(myApiUrl + spec);

        // Check if protocol is using SSL
        if(url.getProtocol().equals("https")) {
            HttpsURLConnection sslconn = (HttpsURLConnection) url.openConnection();

            // Initialize and set SSL context to trust all
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new javax.net.ssl.TrustManager[] { new TrustAllTrustManager() }, new  java.security.SecureRandom());
            sslconn.setSSLSocketFactory(sslContext.getSocketFactory());

            // Set sessionId cookie
            sslconn.setRequestProperty("Cookie", "webpy_session_id=" + sessionId);

            return (HttpURLConnection) sslconn;
        } else {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set sessionId cookie
            conn.setRequestProperty("Cookie", "webpy_session_id=" + sessionId);

            return conn;
        }
    }

    /**
     * Returns the text content of an HTTPURLConnection response
     * @param conn : HttpURLConnection instance from which reads content
     * @return String with the content of the HTTP response
     * @throws Exception
     */
    static String readHTTPContent(HttpURLConnection conn) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();

        // Read line per line and store in StringBuilder
        String line;
        while((line = br.readLine()) != null) 
            result.append(line);

        if(result.length() == 0)
            throw new Exception("No content !");

        return result.toString();

    }

    /**
     * Class used to deserialize the authentication status returned by the INGInious API
     */
    private class InternalAuthStatus
    {
        public boolean authenticated;
        public String username;
    }

    /**
     * Class used to trust every SSL path even if certificate can't be checked
     */
    private class TrustAllTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }
}


