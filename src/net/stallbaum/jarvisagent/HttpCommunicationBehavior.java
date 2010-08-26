/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import jade.core.behaviours.OneShotBehaviour;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class HttpCommunicationBehavior extends OneShotBehaviour implements
		SecurityVocabulary {

	private String convertStreamToString(InputStream is) throws IOException{
		if (is != null){
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		}
		else {
			return "";
		}
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		HttpClient httpclient = new DefaultHttpClient();
				
		//HttpGet httpget = new HttpGet("http://192.168.20.101:8080/ListSensors");
		HttpGet httpget = new HttpGet("http://192.168.20.101:8080/README.txt");
		HttpPost hPost = new HttpPost("http://192.168.20.101:8080/ListSensors");
		
		System.out.println("executing request " + httpget.getRequestLine());
		//System.out.println("executing request " + hPost.getURI());

        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpResponse response = null;
        HttpEntity entity = null;
		try {
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			String responseBody = convertStreamToString(entity.getContent());
			if (entity != null) {
				System.out.println("Response Status: " + response.getStatusLine());
				System.out.println("Response content length: " + responseBody.length());
				System.out.println("Response content type: " + entity.getContentType());
				System.out.println("Response:\n" + responseBody);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Write out request headers
		System.out.println("*** Request Header ***");
		System.out.println("Request Path: " + httpget.getURI());
		System.out.println("Request Query: " + httpget.getRequestLine());
		Header[] requestHeaders = httpget.getAllHeaders();
		for (Header rHeader: requestHeaders){
			System.out.println(rHeader);
		}
		
		// Write out response headers
		System.out.println("*** Response Header ***");
		System.out.println("Status Line: " + response.getStatusLine());
		//System.out.println("Request Query: " + httpget.getRequestLine());
		//Header[] responseHeaders = httpget.getAllHeaders();
		//for (Header rHeader: responseHeaders){
		//	System.out.println(rHeader);
		//}
*/
        System.out.println("----------------------------------------");

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();      
		
	}

}
