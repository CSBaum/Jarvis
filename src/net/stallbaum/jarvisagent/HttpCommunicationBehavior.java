/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jade.core.behaviours.OneShotBehaviour;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.Sensor;
import net.stallbaum.jarvis.util.ontologies.TemperatureData;

/**
 * @author sean
 *
 */
public class HttpCommunicationBehavior extends OneShotBehaviour implements
		SecurityVocabulary {
	
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	JarvisAgent jAgent;
	
	String url;
	int port;
	boolean isInitialized;
	
	public HttpCommunicationBehavior(JarvisAgent agent){
		super();
		jAgent = agent;
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		if (jAgent.agentState != AGENT_HALTING) {
			HttpClient httpclient = new DefaultHttpClient();
					
			// Testing code (remove once things are connected...
			url = "http://192.168.20.100";
			port = 8080;
			isInitialized = true;
			
			
			if (parseSensorList() != 0) {
				System.out.println("Unable to parse sensor list.");
			}
			
			if (!authenticate()){
				System.out.println("Unable to authenticate with robot.");
				jAgent.agentState = AGENT_HALTING;
			}
			
			System.out.println("------- Fake Stuff happening here -------");
			TemperatureData tempData = new TemperatureData(jAgent.getAID(),1);
			tempData.setTemp(Float.parseFloat("72.4"));
			jAgent.newSensordata.add(tempData);
			
			System.out.println("Generated the followiong sensor data: " + tempData);
			
			System.out.println("----------------------------------------");
	
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();      
		}
	}
	
	private boolean authenticate() {
		boolean retCode = false;
		boolean isValid = false;
		
		// -----> Build encrypted URL :)
		SecretKey encKey = new SecretKeySpec(jAgent.agentRobot.getKey(), "AES");

		// Instantiate the cipher
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");		
			cipher.init(Cipher.ENCRYPT_MODE, encKey);

			byte[] encrypted = cipher.doFinal("This is just an example times two".getBytes());
			System.out.println("encrypted string: " + asHex(encrypted));
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Connect to robot's webserver using authentication URL information
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + ":" + port + "/Authenticate?user=jarvis");
		
		// Reviewe response body for authentication key
		HttpResponse response = null;
        HttpEntity entity = null;
		try {
			System.out.println("executing request " + get.getRequestLine());
			response = httpclient.execute(get);
			entity = response.getEntity();
			String responseBody = convertStreamToString(entity.getContent());
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("Response content length: " + responseBody.length());
				System.out.println("Response content type: " + entity.getContentType());
				System.out.println("Response:\n" + responseBody);
				
				// Do the actual body comparison here
				isValid = true;
				if (isValid) {
					get = new HttpGet(url + "/AuthComplete");
					response = httpclient.execute(get);
					if (response.getStatusLine().getStatusCode() != 200) {
						System.out.println("Invalid Response Code: " + response.getStatusLine().getStatusCode());
					}
					else {
						retCode = true;
					}
				}
			}
			else {
				System.out.println("Unable to authenticate. " + response.getStatusLine());
			}
		} catch (ClientProtocolException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
		} catch (IOException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
		}

		return retCode;
	}
	
	private int parseSensorList() {
		int retCode = 0;
		boolean goodResponse = false;
		Integer sensorCount;
		Sensor[] sensors;
		String responseBody = "";
				
		// connect to robot and issue ListSensor command
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + ":" + port + "/ListSensors");
		
		// Reviewe response body for authentication key
		HttpResponse response = null;
        HttpEntity entity = null;
		try {
			System.out.println("executing request " + get.getRequestLine());
			response = httpclient.execute(get);
			entity = response.getEntity();
			responseBody = convertStreamToString(entity.getContent());
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("Response content length: " + responseBody.length());
				System.out.println("Response content type: " + entity.getContentType());
				System.out.println("Response:\n" + responseBody);
				goodResponse = true;
			}
		} catch (ClientProtocolException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
		} catch (IOException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
		}
		
		// convert response body into XML object
		if (goodResponse) {
			try {
				if (responseBody != null) {
					Document xmlContent = stringToDom(responseBody);
					
					Node root = xmlContent.getFirstChild();
					System.out.println("Root Element's local name: " + root.getNodeName());
					
					NamedNodeMap attrs = root.getAttributes();
					int numAttrs = attrs.getLength();
					System.out.println("Root Element has: " + numAttrs + " attributes.");
					for (int inx = 0; inx < numAttrs; inx++){
						Attr attr = (Attr)attrs.item(inx);
						String attrName = attr.getNodeName();
						String attrValue = attr.getNodeValue();
						System.out.println(attrName + " - " + attrValue);
						if (attrName.equalsIgnoreCase("count")){
							sensorCount = Integer.getInteger(attrValue);
						}
					}
					
					System.out.println("***********  Done with Root ****************");
					
					NodeList nList; // = xmlContent.getChildNodes();
					nList = xmlContent.getElementsByTagName("Sensor");
					System.out.println("Found the element 'Sensor' --> " + nList.getLength() + " times.");
					
					Integer robotCount = jAgent.agentRobot.getSensorCount();
					System.out.println("Robot has " + robotCount + " sensors.");
					if (nList.getLength() == robotCount){
						logger.fine("The sensor counts line up!");
					}
					else {
						System.out.println("Agent sent a different number of sensors " 
								  + "than what Jarvis expects.  Robot -> " 
								  + nList.getLength() + " | Jarvis -> " 
								  + jAgent.agentRobot.getSensorCount());
					}
					
					//interateDOM(nList,1);
				}
				else {
					System.out.println("It appears that the response body has become empty ...");
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// loop through objects to 
		
		return retCode;
	}
	
	private void interateDOM(NodeList list, int level) {
		Element element = null;
		Transformer aTransformer = null;
		TransformerFactory tranFactory = TransformerFactory.newInstance(); 
        try {
			aTransformer = tranFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
		for(int i = 0; i < list.getLength(); i++) {
			Node childNode = list.item(i);
			//element = (Element)childNode;
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
			//if (!childNode.getNodeName().equalsIgnoreCase("#text")){
				System.out.println("Node no: " + i + " - " + level + " is " + childNode.getNodeName());
				Source src = new DOMSource(childNode); 
				System.out.println("Its corresponding xml representation:");
				Result dest = new StreamResult(System.out);
				try {
					aTransformer.transform(src, dest);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\n");
				
				NodeList children = childNode.getChildNodes();
				if (children != null){
					interateDOM(children, level++);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
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
	
	public static Document stringToDom(String xmlSource) throws SAXException,
			ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xmlSource)));
	}

	/**
     * Turns array of bytes into string
     *
     * @param buf	Array of bytes to convert to hex string
     * @return	Generated hex string
     */
     public static String asHex (byte buf[]) {
      StringBuffer strbuf = new StringBuffer(buf.length * 2);
      int i;

      for (i = 0; i < buf.length; i++) {
       if (((int) buf[i] & 0xff) < 0x10)
	    strbuf.append("0");

       strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
      }

      return strbuf.toString();
     }
}
