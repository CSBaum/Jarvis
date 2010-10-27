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
import java.util.ArrayList;

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

import jade.content.lang.xml.XMLCodec;
import jade.content.onto.Ontology;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.Sensor;
import net.stallbaum.jarvis.util.ontologies.SensorData;
import net.stallbaum.jarvis.util.ontologies.TemperatureData;

/**
 * @author sean
 *
 */
public class HttpCommunicationBehavior extends WakerBehaviour implements
		SecurityVocabulary {
	
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	JarvisAgent jAgent;
	
	String url;
	int port;
	boolean isInitialized;
	boolean isError = false;
	
	public HttpCommunicationBehavior(JarvisAgent agent, long sleepTime){
		super(agent, sleepTime);
		jAgent = agent;
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void onWake() {
		if (jAgent.agentState != AGENT_HALTING) {
			HttpClient httpclient = new DefaultHttpClient();
					
			// Testing code (remove once things are connected...
			//url = "http://192.168.20.104";
			url = "http://localhost";
			port = 8080;
			isInitialized = true;
			
			
			if (parseSensorList() != 0) {
				System.out.println("Unable to parse sensor list.");
			}
			
			/*if (!authenticate()){
				System.out.println("Unable to authenticate with robot.");
				jAgent.agentState = AGENT_HALTING;
			}*/
			
			System.out.println("==================== Data Pull ========================");
			if (!isError) {
				if (!parseData()){
					System.out.println("Unable to parse data from robot.");
					jAgent.agentState = AGENT_HALTING;
				}
			
				System.out.println("==================== Data Pull Done ========================");
				System.out.println("JarvisAgent know of " + jAgent.newSensordata.size() + " new sensor data readings...");
		
		        // When HttpClient instance is no longer needed, 
		        // shut down the connection manager to ensure
		        // immediate deallocation of all system resources
		        httpclient.getConnectionManager().shutdown();
			}
		}
	}
	
	private void sendProblem(Problem prob){
		// -------> Send a reply
		Ontology ontology = SecurityOntology.getInstance();
		jAgent.getContentManager().registerOntology(ontology);
		jAgent.getContentManager().registerLanguage(new XMLCodec());
		
		ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
		msg.setSender(jAgent.getAID());
		msg.addReceiver(jAgent.getSender());
		msg.setLanguage(XMLCodec.NAME);
		msg.setConversationId(jAgent.getConversationId());
		msg.setPerformative(ACLMessage.FAILURE);
		try {
			msg.setContentObject(prob);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//logger.warning("About to send message:\n" + msg);
		myAgent.send(msg);
		
		// Now shutdown behaviour
		//jAgent.agentState = AGENT_HALTING;
		isError = true;
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
			Problem problem = new Problem(CIPHER_ERROR, e1.getLocalizedMessage());
			sendProblem(problem);
		} catch (NoSuchPaddingException e1) {
			Problem problem = new Problem(CIPHER_ERROR, e1.getLocalizedMessage());
			sendProblem(problem);
		} catch (InvalidKeyException e) {
			Problem problem = new Problem(CIPHER_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		} catch (IllegalBlockSizeException e) {
			Problem problem = new Problem(CIPHER_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		} catch (BadPaddingException e) {
			Problem problem = new Problem(CIPHER_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
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
			Problem problem = new Problem(CLIENT_PROTOCAL_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		} catch (IOException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
			Problem problem = new Problem(IO_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
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
			Problem problem = new Problem(CLIENT_PROTOCAL_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		} catch (IOException e) {
			logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
			Problem problem = new Problem(IO_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
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
					//System.out.println("Found the element 'Sensor' --> " + nList.getLength() + " times.");
					
					if (jAgent.agentRobot != null){
						Integer robotCount = jAgent.agentRobot.getSensorCount();
						System.out.println("Robot has " + robotCount + " sensors.");
						if (nList.getLength() == robotCount){
							logger.finer("The sensor counts line up!");
						}
						else {
							logger.warning("Agent sent a different number of sensors " 
									  + "than what Jarvis expects.  Robot -> " 
									  + nList.getLength() + " | Jarvis -> " 
									  + jAgent.agentRobot.getSensorCount());
						}
					}
					interateDOM(nList,1);
				}
				else {
					System.out.println("It appears that the response body has become empty ...");
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				logger.warning("Unable to parse sensor data: " + e.getLocalizedMessage());
				Problem problem = new Problem(XML_PARSE_ERROR, e.getLocalizedMessage());
				sendProblem(problem);
			} catch (ParserConfigurationException e) {
				logger.warning("Unable to parse sensor data: " + e.getLocalizedMessage());
				Problem problem = new Problem(XML_PARSE_ERROR, e.getLocalizedMessage());
				sendProblem(problem);
			} catch (IOException e) {
				logger.warning("Unable to parse sensor data: " + e.getLocalizedMessage());
				Problem problem = new Problem(IO_ERROR, e.getLocalizedMessage());
				sendProblem(problem);
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
				//System.out.println("Node no: " + i + " - " + level + " is " + childNode.getNodeName());
				Source src = new DOMSource(childNode); 
				//System.out.println("Its corresponding xml representation:");
				Result dest = new StreamResult(System.out);
				try {
					aTransformer.transform(src, dest);
				} catch (TransformerException e) {
					Problem problem = new Problem(XML_PARSE_ERROR, e.getLocalizedMessage());
					sendProblem(problem);
				}
				System.out.println("\n");
				
				NodeList children = childNode.getChildNodes();
				if (children != null){
					interateDOM(children, level++);
				}
			}
		}
	}
	
	private boolean parseData(){
		boolean result = false;
		boolean goodResponse = false;
		String responseBody = "";
				
		// connect to robot and issue ListSensor command
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + ":" + port + "/GetData");
		
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
			//logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
			Problem problem = new Problem(CLIENT_PROTOCAL_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		} catch (IOException e) {
			//logger.severe("Unable to communucate with robot: " + e.getLocalizedMessage());
			Problem problem = new Problem(IO_ERROR, e.getLocalizedMessage());
			sendProblem(problem);
		}
		
		if (goodResponse){
			
			//-----> Allocate array list if this is 1st time through
			if (jAgent.newSensordata == null){
				jAgent.newSensordata = new ArrayList<SensorData>();
			}
			
			//-----> Check for content in the msg body
			if (responseBody != null) {
				int dataCount = 0;
				Document xmlContent;
				
				try {
					xmlContent = stringToDom(responseBody);
					Node root = xmlContent.getFirstChild();
					logger.fine("Root Element's local name: " + root.getNodeName());
					
					NamedNodeMap attrs = root.getAttributes();
					int numAttrs = attrs.getLength();
					logger.fine("Root Element has: " + numAttrs + " attributes.");
					
					for (int inx = 0; inx < numAttrs; inx++){
						Attr attr = (Attr)attrs.item(inx);
						String attrName = attr.getNodeName();
						String attrValue = attr.getNodeValue();
						System.out.println(attrName + " - " + attrValue);
						if (attrName.equalsIgnoreCase("count")){
							dataCount = Integer.parseInt(attrValue);
						}
					}
					
					NodeList dataBits = root.getChildNodes();
					int childCount = 0;
					for (int inx = 0; inx<dataBits.getLength();inx++){
						if (dataBits.item(inx) instanceof Element){
							Element elem = (Element)dataBits.item(inx);
							
							if (elem.getNodeName().equalsIgnoreCase("SensorData")){
								childCount++;
								SensorData sensorData = null;
								NamedNodeMap sAttr = elem.getAttributes();
								
								System.out.println(elem.getNodeName() + " has " + sAttr.getLength() + " attributes:");
								for (int knx = 0; knx < sAttr.getLength();knx++){
									Attr attr = (Attr)sAttr.item(knx);
									String attrName = attr.getNodeName();
									String attrValue = attr.getNodeValue();
									System.out.println(attrName + " - " + attrValue);
									if (attrName.equalsIgnoreCase("type")){
										if (attrValue.contentEquals("temp")){
											//TODO This needs to be moved out of the foor loop 'cause we need to get
											//     both Type and ID from XML.
											sensorData = new TemperatureData(myAgent.getAID(), SensorData.TEMPERATURE_SENSOR, 1);
											// TODO Change this to actual data from robot settings ...
											sensorData.setIsArchived(true);
											System.out.println("The sensor type is: " + sensorData.getType());
										}
									}
								}
								
								NodeList data = elem.getChildNodes();
								
								for(int jnx = 0;jnx<data.getLength();jnx++){
									if (data.item(jnx) instanceof Element){
										Element sData = (Element)data.item(jnx);
										System.out.println("sData name: " + sData.getNodeName());
										
										if (sData.getNodeName().equalsIgnoreCase("value")){
											String value = sData.getTextContent();
											//Check type
											System.out.println("The sensor type is: " + sensorData.getType());
											switch(sensorData.getType()){
												case 1:
													TemperatureData tData = (TemperatureData)sensorData;
													tData.setTemp(Float.parseFloat(value));
													sensorData = tData;
													jAgent.newSensordata.add(tData);
													break;
												default:
													System.out.println("Invalid sensor type.");
											}
										}
									}
								}
								
								logger.finer("Created the following obj:" + sensorData);
							}
						}
					}
					
					if (childCount != dataCount){
						System.out.println("Sensor data read error. Count mismatch: Expected - " + dataCount + " Actual - " + childCount);
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
			
			result = true;
		}
		
		return result;
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
