/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.FieldPosition;
import java.text.NumberFormat;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.stallbaum.jarvis.util.ontologies.AlertConfirmation;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.Sensor;
import jade.core.AID;
import jade.core.Agent;
import jade.util.Logger;

/**
 * @author Sean
 *
 */
public class PlayerAgent extends AbsJAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1233712675363055824L;
	static NumberFormat fmt = NumberFormat.getInstance ();
	
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	protected int agentState = AGENT_INITIALIZING;
	protected int previousAgentState = AGENT_INITIALIZING;
	
	boolean isInitialized = false;
	
	// Information that we need to actually talk to a robot
	private String url = "";
	private int port = 0;
	
	Sensor[] sensors;

	/**
	 * 
	 */
	public PlayerAgent() {
		// TODO Auto-generated constructor stub
	}

	protected void setup() {
		logger.info("Starting PlayerAgent: " + getAID().getName());
		
		// We need to setup behavior to listen for messages from controlling 
		//    JarvisAgent
		AgentPlayerCommBehavior apcb = new AgentPlayerCommBehavior(this, 250);
		addBehaviour(apcb);
		
		addBehaviour(new ShutdownRobotAgent(this, 250, apcb));
		
		// Testing code (remove once things are connected...
		url = "http://192.168.20.101";
		port = 8080;
		isInitialized = true;
		
		do {
			logger.info("Waiting for agemt to send initialization information....");
			doWait(1000);
		} while (!isInitialized);
		
		if (parseSensorList() != 0) {
			System.out.println("Unable to parse sensor list.");
		}
		
		if (!authenticate()){
			System.out.println("Unable to authenticate with robot.");
			agentState = AGENT_HALTING;
		}
	}
	
	private boolean authenticate() {
		boolean retCode = false;
		boolean isValid = false;
		
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
					}
					
					System.out.println("***********  Done with Root ****************");
					
					NodeList nList; // = xmlContent.getChildNodes();
					nList = xmlContent.getElementsByTagName("Sensor");
					System.out.println("Found the element 'Sensor' --> " + nList.getLength() + " times.");
					interateDOM(nList,1);
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

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getConversationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlertId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AID getReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AID getSender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSender(AID _sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAlertStataus(boolean _flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAlertStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAlertConfirmation(AlertConfirmation _confirm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AlertConfirmation getAlertConfirmation() {
		// TODO Auto-generated method stub
		return null;
	}
}
