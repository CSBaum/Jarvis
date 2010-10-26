/**
 * 
 */
package net.stallbaum.jarvis;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import net.stallbaum.jarvis.gui.MainFrame;
import net.stallbaum.jarvis.util.Archiver;
import net.stallbaum.jarvis.util.WakeOnLan;
import net.stallbaum.jarvis.util.ontologies.Motor;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.Sensor;
import net.stallbaum.jarvis.util.ontologies.Tire;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

//import jess.*;

/**
 * @author Sean Stallbaum
 *
 */
public class Jarvis extends GuiAgent implements SecurityVocabulary{

	private AID[] jarvisAgents = null;
	private Hashtable<AID, Robot> jarvisAgents2 = new Hashtable<AID,Robot>();
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	//Testing
	
	private static final long serialVersionUID = 4760582118511377080L;
	
	private Connection conn;
	private ResultSet agentRS = null;
	private Statement stmt = null;
	
	private int numberOfRows = 0;
	
	protected Archiver archive;
	
	//---------> System State information
	protected int systemState = SYSTEM_INITIALIZING;
	protected int lastSystemState = SYSTEM_INITIALIZING;
	
	protected int securityLevel = SECURITY_LEVEL_OFF;
	protected int lastSecurityLevel = SECURITY_LEVEL_OFF;
	
	// ---------> House keeping variables
	protected Vector<String> activeAgents = new Vector<String>();
	protected Vector<AID>agentListing = new Vector<AID>();
	
	private HashSet<AID> activeAgentsHSet = new HashSet<AID>();
	private HashSet<AID> agentListingHSet = new HashSet<AID>();
	
	protected Set<AID> activeAgentsSet = Collections.synchronizedSet(activeAgentsHSet);
	protected Set<AID> agentListingSet = Collections.synchronizedSet(agentListingHSet);
	
	protected String alertId = "";
	
	// --------------- GUI Variables
	static final int WAIT = -1;
	static final int SHUTDOWN = 1;
	static final int ADD_AGENT = 2;
	static final int SECURITY_OFF = 10;
	private int command = WAIT;
	
	// --------------- JADE Codex variables
	private Codec codec = new SLCodec();
	private Ontology ontology = SecurityOntology.getInstance();
	
	transient protected MainFrame jGui;

	public void addActiveAgent(AID _AID){
		activeAgentsSet.add(_AID);
	}
	
	/**
	 * Method used by JADE to initialize the agent ...
	 * 
	 *  NOTE: Avoid constructor since framework may not be initialized in time
	 */
	protected void setup(){
		logger.info("Hello! Jarvis server agent: " + getAID().getName() + " is starting");
		
		// Process configuration information
		
		// Configure / Connect to DB
		try {
			// Load MySQL Driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			// Create connection (Should be based on property file)
			conn = DriverManager.getConnection("jdbc:mysql://localhost/jarvis",
			                                   "jarvis","stark");
			
			stmt = conn.createStatement();
		    agentRS = stmt.executeQuery("SELECT a.UID, a.name, a.class, a.type, b.MAC, b.host, b.port, b.robot_id " +
										"FROM agents a, containers b " + 
										"WHERE a.UID = b.agent_id AND b.enabled = '1'");
		    agentRS.last();
		    numberOfRows = agentRS.getRow();
		    logger.fine("Jarvis knows about: " + numberOfRows + " agents");
		    agentRS.beforeFirst();
		    
		    ResultSetMetaData metadata;
		    metadata = agentRS.getMetaData();
		    /*
		    for(int inx = 0; inx < metadata.getColumnCount(); inx++){
		    	String colName = metadata.getColumnName(inx);
		    	String colTypeName = metadata.getColumnTypeName(inx);
		    	logger.info("Column Name: " + colName + " is " + colTypeName);
		    	
		    }*/
		} catch (SQLException sex){
			// handle any errors
			logger.severe("SQL Error occured while setting up Jarvis:\n" +
						  "         SQLException: " + sex.getMessage() + "\n" +
						  "         SQLState: " + sex.getSQLState() + "\n" +
						  "         VendorError: " + sex.getErrorCode());
		}catch (Exception ex){
			logger.severe("Unable to setup database:\n\t" + 
						  ex.getLocalizedMessage() + "\n\t"+ 
						  ex.getStackTrace().toString());
			
			//------>Remove agent from container
			try {
				logger.warning("Removing " + getLocalName() + " from the container.");
				ContainerController cc = getContainerController();
				AgentController ac;
				ac = cc.getAgent(getLocalName());
				ac.kill();
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				logger.severe("Unable to remove " + getLocalName() + 
							  " from Container.\n\t" + e.getLocalizedMessage());
			}		
		}finally {
		}
		
		// Look for agents
		Vector<String> ignoreList = new Vector<String>();
		checkForActiveAgents(numberOfRows, ignoreList);
		
		// Generate AlertID
		alertId = genCID();
		
		// Add Agent Behaviors
		try
		{
			// Add behavior(s) to handle communication with each agent
			if (jarvisAgents != null){
				System.out.println("Number of agents in array " + jarvisAgents.length);
			} else {
				logger.warning("There are no agent entries, we shuld rerun through the above code again ...");
			}
			
			doWait(2000);
			
			// Register new behaviors for each agent
			for (AID JAgent : jarvisAgents){
				logger.fine("Processing: " + JAgent.getLocalName());
				Robot r = jarvisAgents2.get(JAgent);
				agentListing.add(JAgent);
				addBehaviour(new JarvisAgentCommunication(this, 4000 , JAgent, r, alertId));
			}
			
			//------> Added shutdown behaviour
		} catch (NullPointerException nex) {
			logger.severe("Error occured while running -> Jarvis Agent Setup - Null Pointer detected.");
		} catch (Exception ex){
			logger.severe("Error occured while running -> Jarvis Agent Setup -\n\t" + ex.getMessage());
		}
		
		// Add system command listener
		addBehaviour(new JarvisCommandListener(this));
		addBehaviour(new ShutdownAgent(this, 250));
		
		//----> COnfigure Archiver class
		try {
			archive = new Archiver(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.severe("Unable to initialize Archiver class: " + e.getLocalizedMessage());
			systemState = SYSTEM_HALTING;
		}
		
		// ------- Launch GUI
		if (systemState != SYSTEM_HALTING) {
			jGui = new MainFrame(this, agentRS);
			//inst.setDefaultCloseOperation(EXIT_ON_CLOSE);
			jGui.setLocationRelativeTo(null);
			jGui.setVisible(true);
			
			doWait(2000);
			systemState = SYSTEM_STANDBY;
			alertGui(getSystemStateTxt());
		}
	}
	
	protected void takedown() {
		// Send out server going down message
		//ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		//for (AID JAgent : jarvisAgents){
		//	msg.addReceiver(JAgent);
		//}
		//msg.setContent("Jarvis-Halt");
		//System.out.println("Jarvis: Sending message: " + msg.getContent());
		//send(msg);
		
		// Clean up SQL connections
		 if (agentRS != null) {
			 try {
				 agentRS.close();
			 } catch (SQLException sqlEx) { } // ignore

			 agentRS = null;
		 }

		 if (stmt != null) {
			 try {
				 stmt.close();
			 } catch (SQLException sqlEx) { } // ignore

			 stmt = null;
		 }
		 
		 System.out.println(getLocalName() + " is now shutting down.");
		if (jGui != null) {
			jGui.setVisible(false);
			jGui.dispose();
		}

	}
	
	private void checkForActiveAgents(int _numberOfRows, Vector<String> ignoreList){
		
		int rsCount = _numberOfRows;
		//System.out.println("============================");
		//System.out.println("Starting to look for agents!");
		//System.out.println("============================");

		String agentName = "";
		String agentType = "";
		String agentMac = "";
		String agentHost = "";
		String agentPort = "";
		Integer robotId;
		
		Robot robot = null;
		
		// Update the list of JAgents agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jarvis-agent-robot");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			
			logger.fine("Found " + result.length + " agent(s) in the DF");
			
			if (result.length < 1) {
				int inx = 0;
				// Initialize ALL agents from RS
				logger.info("No Agents were found. Initializing all via WakeOnLan.");
				jarvisAgents = new AID[rsCount];
				while (agentRS.next()) {
					agentName = agentRS.getString("name");
					agentType = agentRS.getString("type");
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
					robotId = agentRS.getInt("robot_id");
					
					logger.finer("Agent Info" + 
							   "\n\tName:" + agentName +
							   "\n\tType: " + agentType + 
							   "\n\tMAC:" + agentMac +
							   "\n\tHost:" + agentHost + 
							   "\n\tPort:" + agentPort +
							   "\n\tRobot Id:" + robotId);

					if (agentType.equalsIgnoreCase("robot")){
						logger.info("About to wake up -> " + agentName);
					
						WakeOnLan wol = new WakeOnLan();
						wol.wakeUp(agentHost, agentMac);
						
						robot = generateRobot(robotId);
					}
					else {
						// do network agenit initialization code here
					}
					
					// Build AID name for the newly woken up name
					jarvisAgents2.put(new AID(agentName, false), robot);
					jarvisAgents[inx] = new AID(agentName, false);
					inx++;
				}
				
			} else if (result.length < rsCount){
				boolean isMissing = true;
				logger.info("Found " + result.length + " jarvis agents out of " + rsCount + " agents in the db");
				jarvisAgents = new AID[rsCount];
				while (agentRS.next()) {
					isMissing = true;
					agentName = agentRS.getString("name");
					agentType = agentRS.getString("type");
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
					robotId = agentRS.getInt("robot_id");
					
					logger.finer("Agent Info" + 
							   "\n\tName:" + agentName +
							   "\n\tType: " + agentType + 
							   "\n\tMAC:" + agentMac +
							   "\n\tHost:" + agentHost + 
							   "\n\tPort:" + agentPort +
							   "\n\tRobot Id:" + robotId);
					
					for (int i = 0; i < rsCount; ++i) {
						if (ignoreList.contains(result[i].getName())){
							logger.info("Agent found on ignore List ... skipping it ...");
							isMissing = false;
						} else {
							if (agentName.equalsIgnoreCase(result[i].getName().getLocalName())){
								if (robotId != null) {
									robot = generateRobot(robotId);
									jarvisAgents2.put(result[i].getName(), robot);
								}
								jarvisAgents[i] = result[i].getName();
								isMissing = false;
							}
						}
					}
					if (isMissing){
						if (agentType.equalsIgnoreCase("robot")) {
							logger.info("About to wake up -> " + agentName + 
									   " on MAC -> " + agentMac + " with host of -> " +
									   agentHost + " and a port of -> " + agentPort);
							
							// call Wake on Lan class to wake up container
							WakeOnLan wol = new WakeOnLan();
							wol.wakeUp(agentHost, agentMac);
						}
						else {
							// do network agent initialization code here
						}
					}
				}
			}
			else {
				// Everyone is up and running ... maybe 
				//    ... verify valid agents
				logger.info("Found " + result.length + " jarvis agent(s)");
				jarvisAgents = new AID[rsCount];
				while (agentRS.next()){
					agentName = agentRS.getString("name");
					agentType = agentRS.getString("type");
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
					robotId = agentRS.getInt("robot_id");
					
					logger.finer("Agent Info" + 
								   "\n\tName:" + agentName +
								   "\n\tType: " + agentType + 
								   "\n\tMAC:" + agentMac +
								   "\n\tHost:" + agentHost + 
								   "\n\tPort:" + agentPort +
								   "\n\tRobot Id:" + robotId);
					
					for (int i = 0; i < result.length; ++i) {
						if (agentName.equalsIgnoreCase(result[i].getName().getLocalName())){
							if (robotId != null){
								robot = generateRobot(robotId);
								jarvisAgents2.put(result[i].getName(), robot);
							}
							jarvisAgents[i] = result[i].getName();
							logger.info(getLocalName() + ":Adding Agent --> " + result[i].getName().getLocalName());
						}
						else { 
							// We need to throw an error and not add it to 
							//     the AID array
							logger.info(getLocalName() + ":Found agent: " + 
											   result[i].getName().getLocalName() + 
											   " is not in the database... ignoring it!");
							//ignoreList.add(result[i].getName().toString());
							//checkForActiveAgents(numberOfRows -1, ignoreList);
						}
					}
				}
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
			logger.severe("FIPA Exception - " + fe.getLocalizedMessage());
		} catch (SQLException sex) {
			System.out.println("SQLException: " + sex.getMessage());
			System.out.println("SQLState: " + sex.getSQLState());
			System.out.println("VendorError: " + sex.getErrorCode());
		} 
	}
	
	// -------------------- GUI Methods
	protected void onGuiEvent(GuiEvent ev) {
		// ----------------------------------------  Receive user command via the gui

		command = ev.getType();
		logger.finer("GuiEvent: " + command);
		//if (systemState == SYSTEM_HALTING) {
		//	//alertGui("Bye!");
		//	doDelete();
		//	System.exit(0);
		//}
		if (command == GUI_SHUTDOWN) {
			systemState = SYSTEM_HALTING;
			logger.info("GUI sent Shutdown command.");
			alertGui(getSystemStateTxt());
			//doDelete();
			//System.exit(0);
		}
		else if (command == GUI_ADD_AGENT) {
			logger.fine(getLocalName() + ": GUI has requested adding a new agent.");
			// -------> Get parameters from event 

			// -------> Insert data into appropriate tables
			
			// -------> Launch Agent on remote platform
			
			// -------> Add agent communication behavior
		}
		else if (command == GUI_REMOVE_AGENT) {
			
		}
		else if (command == GUI_SEC_OFF){
			logger.fine(getLocalName() + ": GUI sent Security Off command.");
		
			// -------> Need to update the system status to standby
			lastSystemState = systemState;
			systemState = SYSTEM_STANDBY;
			alertGui(getSystemStateTxt());
			
			// -------> Send out messages to all agents
			lastSecurityLevel = securityLevel;
			securityLevel = SECURITY_LEVEL_OFF; 
			logger.info(getLocalName() + ": Security Level - " + securityLevel);
			logger.info(getLocalName() + ": Last Security Level - " + lastSecurityLevel);
			
			// -------> Add a run once behaviour that sleeps for 10 seconds 
			//               and changes levels so they equal
			addBehaviour(new SecurityResetBehaviour(this, 5000));
		}
		else if (command == GUI_SEC_NET){
			logger.fine(getLocalName() + ": GUI sent Security Network Agents Only command.");
			
			// -------> Update System State
			lastSystemState = systemState;
			systemState = SYSTEM_SECURITY_NETONLY;
			alertGui(getSystemStateTxt());
			
			// -------> Update Security Level
			lastSecurityLevel = securityLevel;
			securityLevel = SECURITY_LEVEL_NETWORK_AGENTS_ONLY; 
			logger.info(getLocalName() + ": Security Level - " + securityLevel);
			logger.info(getLocalName() + ": Last Security Level - " + lastSecurityLevel);
			
			// -------> Add a run once behaviour that sleeps for 10 seconds 
			//               and changes levels so they equal
			addBehaviour(new SecurityResetBehaviour(this, 5000));
		}
		else if (command == GUI_SEC_BOT){
			logger.fine("GUI sent Security Robot Agents Only command.");
			
			lastSystemState = systemState;
			systemState = SYSTEM_SECURITY_ROBOTONLY;
			alertGui(getSystemStateTxt());
			
			lastSecurityLevel = securityLevel;
			securityLevel = SECURITY_LEVEL_ROBOT_AGENTS_ONLY; 
			logger.info(getLocalName() + ": Security Level - " + securityLevel);
			logger.info(getLocalName() + ": Last Security Level - " + lastSecurityLevel);
			
			// -------> Add a run once behaviour that sleeps for 10 seconds 
			//               and changes levels so they equal
			addBehaviour(new SecurityResetBehaviour(this, 5000));
		}
		else if (command == GUI_SEC_ON){
			logger.fine("GUI sent Security On command.");
			
			lastSystemState = systemState;
			systemState = SYSTEM_SECURITY_ALL;
			alertGui(getSystemStateTxt());
			
			lastSecurityLevel = securityLevel;
			securityLevel = SECURITY_LEVEL_ALL_ON; 
			logger.info(getLocalName() + ": Security Level - " + securityLevel);
			logger.info(getLocalName() + ": Last Security Level - " + lastSecurityLevel);
			
			// -------> Add a run once behaviour that sleeps for 10 seconds 
			//               and changes levels so they equal
			addBehaviour(new SecurityResetBehaviour(this, 5000));
		}
		else if (command == GUI_RESET) {
			logger.info("GUI sent System Reset command.");
		}
		else {
			logger.info("GUI Send unsupport command: " + command);
		}
	}

	protected void alertGui(Object response) {
		// --------------------------------  Process the response of the server
		//		                                   to the gui for display
		jGui.alertResponse(response);
	}

	/*/--------------------------- Utility methods ----------------------------//
	protected void lookupServer() {
		// ---------------------  Search in the DF to retrieve the server AID

		AID server;
		ServiceDescription sd = new ServiceDescription();
		sd.setType(SERVER_AGENT);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			if (dfds.length > 0 ) {
				server = dfds[0].getName();
				alertGui("Localized server");
			}
			else { 
				alertGui("Unable to localize server. Please try later!");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed searching int the DF!");
		}
	}*/

	/**
	 * 
	 * @param server
	 * @param performative
	 * @param action
	 */
	protected void sendMessage(AID server, int performative, AgentAction action) {
		// --------------------------------------------------------
		ACLMessage msg = new ACLMessage(performative);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		try {
			getContentManager().fillContent(msg, new Action(server, action));
			msg.addReceiver(server);
			send(msg);
			alertGui("Contacting server... Please wait!");
		}
		catch (Exception ex) { 
			logger.warning("Unable to send message to " + server.getName() + 
					" - " + ex.getLocalizedMessage()); 
		}
	}

	/**
	 * @return the systemState
	 */
	public int getSystemState() {
		return systemState;
	}
	
	public String getSystemStateTxt() {
		String state = "";
		
		switch(systemState){
			case SYSTEM_INITIALIZING: 
				state = "System Initalizing";
				break;
			case SYSTEM_STANDBY: 
				state = "Standy";
				break;
			case SYSTEM_SECURITY_NETONLY: 
				state = "Network Only Mode";
				break;
			case SYSTEM_SECURITY_ROBOTONLY: 
				state = "Robot Only Mode";
				break;
			case SYSTEM_SECURITY_ALL: 
				state = "Full Security Mode";
				break;
			case SYSTEM_HALTING:
				state = "System Halting";
				break;
			case SYSTEM_ALARM:
				state = "System Alarm!";
			default: 
				state = "Invalid State ... " + systemState;
				logger.warning("Invalid System State - " + systemState);
		}
		
		return state;
	}
	
	/**
	 * 
	 * @param robotId - id of the robot to look up
	 * @return populated robot object, null on error
	 */
	private Robot generateRobot(Integer robotId) {
		Robot robot = new Robot();
		String query = "Select * from robots where UID = " + robotId;
		
		String name = "";
		String cfgFile = "";
		String sensorList = "";
		Integer tireType;
		Integer tireCount;
		Integer motorType;
		byte[] key;
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next()){
				//----- Get all the data out of the result set row
				name = rset.getString("name");
				cfgFile = rset.getString("cfgFile");
				sensorList = rset.getString("sensors");
				tireCount = rset.getInt("tireCount");
				tireType = rset.getInt("tireType");
				motorType = rset.getInt("motorType");
				key = rset.getBytes("commKey");
				
				//----- Fill in core robot info
				robot.setName(name);
				robot.setCfgFile(cfgFile);
				robot.setTireCount(tireCount);
				robot.setKey(key);
				
				//---- Lookup Motor Information
				Motor motor = lookupMotor(motorType);
				robot.setMotorType(motor);
				
				//----- Look up Tire information
				Tire tire = lookupTire(tireType);
				robot.setTireType(tire);
				
				// ----- Check if sensor lists are empty, 
				//       if not call sensor lookup
				if (sensorList != null){
					//----- Need to sort out where all the sensors are located
					String front_center, front_left, front_right;
					String left_front, left_middle, left_back;
					String right_front, right_middle, right_back;
					String back_center, back_left, back_right;
					
					//----- Look for front facing sensors and add them
					front_center = rset.getString("sensors-front-center");
					if (front_center != null){
						String[] sensorIds = front_center.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setFrontCenterSensor(sensor);
						}
					}
					
					front_left = rset.getString("sensors-front-left");
					if (front_left != null){
						String[] sensorIds = front_left.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setFrontLeftSensor(sensor);
						}
					}
					
					front_right = rset.getString("sensors-front-right");
					if (front_right != null){
						String[] sensorIds = front_right.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setFrontRightSensor(sensor);
						}
					}
					
					//----- Look for left facing sensors and add them
					left_front = rset.getString("sensors-left-front");
					if (left_front != null){
						String[] sensorIds = left_front.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setLeftFrontSensor(sensor);
						}
					}
					
					left_middle = rset.getString("sensors-left-middle");
					if (left_middle != null){
						String[] sensorIds = left_middle.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setLeftMiddleSensor(sensor);
						}
					}
					
					left_back = rset.getString("sensors-left-back");
					if (left_back != null){
						String[] sensorIds = left_back.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setFrontRightSensor(sensor);
						}
					}
					
					//-----  Look for right facing sensors and add them
					right_front = rset.getString("sensors-right-front");
					if (right_front != null){
						String[] sensorIds = right_front.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setRightFrontSensor(sensor);
						}
					}
					
					right_middle = rset.getString("sensors-right-middle");
					if (right_middle != null){
						String[] sensorIds = right_middle.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setRightMiddleSensor(sensor);
						}
					}
					
					right_back = rset.getString("sensors-right-back");
					if (right_back != null){
						String[] sensorIds = right_back.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setRightBackSensor(sensor);
						}
					}
					
					//----- Look for back facing sensors and add them
					back_center = rset.getString("sensors-back-center");
					if (back_center != null){
						String[] sensorIds = back_center.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setBackCenterSensor(sensor);
						}
					}
					
					back_left = rset.getString("sensors-back-left");
					if (back_left != null){
						String[] sensorIds = back_left.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setBackLeftSensor(sensor);
						}
					}
					
					back_right = rset.getString("sensors-back-right");
					if (back_right != null){
						String[] sensorIds = back_right.split(",");
						for(String sensorId: sensorIds) {
							Sensor sensor = lookupSensor(new Integer(sensorId));
							robot.setBackRightSensor(sensor);
						}
					}
				}
				else {
					// No sensors defined
				}
			}
			
		} catch (SQLException e) {
			logger.severe("Unable to lookup robot(" + robotId + "). Error Code: " + 
						  e.getErrorCode() + "\nError Msg: " + 
						  e.getLocalizedMessage());
		}
		
		//logger.finer("Constructed this robot object --> " + robot);
		
		return robot;
	}
	
	private Sensor lookupSensor(Integer sensorId){
		Sensor sensor = new Sensor();
		String name = "";
		String description = "";
		String type = "";
		Integer refreshRate;
		String fieldOfView = "";
		String resolution = "";
		String minRange = "";
		String maxRange = "";
		
		String query = "SELECT * FROM sensors WHERE UID = " + sensorId;
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while (rset.next()){
				name = rset.getString("name");
				description = rset.getString("description");
				type = rset.getString("type");
				refreshRate = rset.getInt("refreshRate");
				fieldOfView = rset.getString("fieldOfView");
				resolution = rset.getString("resolution");
				minRange = rset.getString("minRange");
				maxRange = rset.getString("maxRange");
				
				sensor.setName(name);

				//sensor.setType(type);
				
				if (description != null)
					sensor.setDescription(description);
				
				if (refreshRate != null) 
					sensor.setCycle(refreshRate);
			}
			
		} catch (SQLException e) {
			logger.severe("Unable to query for Sensor ID: " + sensorId);
			sensor = null;
		}
		
		return sensor;
	}
	
	private Motor lookupMotor(Integer _id) {
		Motor motor = new Motor();
		String name;
		String gearRatio;
		Integer rpm;
		String torque;
		
		String query = "SELECT * FROM robot_motors WHERE UID = " + _id;
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next()){
				name = rset.getString("name");
				gearRatio = rset.getString("gear_ratio");
				rpm = rset.getInt("rpm");
				torque = rset.getString("torque");
				
				motor.setName(name);
				motor.setGearRatio(gearRatio);
				motor.setRpm(rpm);
				motor.setTorque(torque);
			}
		} catch (SQLException e) {
			logger.severe("Unable to lookup motor id (" + _id + ").\n" + e.getLocalizedMessage());
			motor = null;
		}
		return motor;
	}
	
	private Tire lookupTire(Integer _id) {
		Tire tire = new Tire();
		String name;
		Float radius;
		
		String query = "SELECT * FROM robot_tires WHERE UID = " + _id;
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next()){
				name = rset.getString("name");
				radius = rset.getFloat("radius");
				
				tire.setName(name);
				tire.setRadius(radius);
			}
		} catch (SQLException e) {
			logger.severe("Unable to lookup motor id (" + _id + ").\n" + e.getLocalizedMessage());
			tire = null;
		}
		return tire;
	}
	
//  --- generating Conversation IDs -------------------

	protected int cidCnt = 0;
	String cidBase ;

	private String genCID() 
	{ 
		if (cidBase==null) {
			cidBase = getLocalName() + hashCode() +
			System.currentTimeMillis()%10000 + "_";
		}
		return  cidBase + (cidCnt++); 
	}

	//  --- generating distinct Random generator -------------------

	//private Random newRandom() 
	//{	return  new Random( hashCode() + System.currentTimeMillis()); }
	
	// This method returns the name of a JDBC type. 
	// Returns null if jdbcType is not recognized.
	/*
	private static String getJdbcTypeName(int jdbcType) { 
		// Use reflection to populate a map of int values to names 
		if (map == null) { 
			map = new HashMap(); 
			
			// Get all field in java.sql.Types 
			Field[] fields = java.sql.Types.class.getFields(); 
			for (int i=0; i<fields.length; i++) { 
				try { 
					// Get field name 
					String name = fields[i].getName(); 
					// Get field value 
					Integer value = (Integer)fields[i].get(null); 
					
					// Add to map 
					map.put(value, name); 
				} 
				catch (IllegalAccessException e) { 
				} 
			} 
		} 
		// Return the JDBC type name 
		return (String)map.get(new Integer(jdbcType)); 
	} */
	
	//static Map map; 
}
