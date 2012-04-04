/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collector;

import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @author pararth
 */
public class FetchURL {
    static String proxyserver;
    static int proxyport;
    static String proxyuser;
    static String proxypass;
    static String proxyfile;
    static Boolean proxyset = true;
    
    protected static void setProxyDetails(){
        if (proxyset) return;
        try {
            proxyserver = "netmon.iitb.ac.in";
            proxyport = 80;
            proxyfile = "/home/sagar/NetBeansProjects/PersonalNews/src/proxydetails.txt";
            BufferedReader in = new BufferedReader(new FileReader(proxyfile));
            String str;
            str = in.readLine();
            proxyuser = str;
            str = in.readLine();
            proxypass = str;
            in.close();
            proxyset = true;
        } catch (IOException e) {
            System.out.println("Exception "+ e + "occured.");
        }
    }
    
    public static String getResponse(String scheme, String host, String path, String query)
        throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            //setProxyDetails();
            /*httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(proxyserver, proxyport),
                    new UsernamePasswordCredentials(proxyuser, proxypass));
            
            HttpHost proxy = new HttpHost(proxyserver, proxyport);
            
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            */
            URI uri = null;
            
            try {
                uri = new URI(scheme,host,path,query,null);
            } catch (URISyntaxException ex) {
                Logger.getLogger(FetchURL.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
            
            HttpGet httpget = new HttpGet(uri.toASCIIString());
            
            System.out.println("executing request: " + httpget.getRequestLine());
            //System.out.println("via proxy: " + proxy);
            
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
