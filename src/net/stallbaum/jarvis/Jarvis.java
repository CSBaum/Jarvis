/**
 * 
 */
package net.stallbaum.jarvis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.stallbaum.jarvis.gui.MainFrame;
import net.stallbaum.jarvis.util.WakeOnLan;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

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

//import jess.*;

/**
 * @author Sean Stallbaum
 *
 */
public class Jarvis extends GuiAgent implements SecurityVocabulary{

	private AID[] jarvisAgents = null;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	private static final long serialVersionUID = 4760582118511377080L;
	
	private Connection conn;
	private ResultSet agentRS = null;
	private Statement stmt = null;
	
	private int numberOfRows = 0;
	
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
		
		//System.out.println("Hello! Jarvis server agent: " + getAID().getName() + " is starting");
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
		    agentRS = stmt.executeQuery("SELECT a.UID, a.name, a.class, b.MAC, b.host, b.port " +
										"FROM agents a, containers b " + 
										"WHERE a.UID = b.agent_id AND b.enabled = '1'");
		    agentRS.last();
		    numberOfRows = agentRS.getRow();
		    logger.fine("Jarvis knows about: " + numberOfRows + " agents");
		    agentRS.beforeFirst();
		} catch (SQLException sex){
			// handle any errors
			System.out.println("SQL Error occured while setting up Jarvis: ");
			System.out.println("         SQLException: " + sex.getMessage());
			System.out.println("         SQLState: " + sex.getSQLState());
			System.out.println("         VendorError: " + sex.getErrorCode());
		}catch (Exception ex){
			logger.severe("Unable to setup database:\n\t" + ex.getMessage() + "\n\t"+ ex.getStackTrace());
		}finally {
		}
		
		// Look for agents
		Vector<String> ignoreList = new Vector<String>();
		checkForActiveAgents(numberOfRows, ignoreList);
		
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
				agentListing.add(JAgent);
				addBehaviour(new JarvisAgentCommunication(this, 4000 , JAgent));
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
		
		// ------- Launch GUI
		jGui = new MainFrame(this, agentRS);
		//inst.setDefaultCloseOperation(EXIT_ON_CLOSE);
		jGui.setLocationRelativeTo(null);
		jGui.setVisible(true);
		
		doWait(2000);
		systemState = SYSTEM_STANDBY;
		alertGui(getSystemStateTxt());
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
		System.out.println("============================");
		System.out.println("Starting to look for agents!");
		System.out.println("============================");

		String agentName = "";
		String agentMac = "";
		String agentHost = "";
		String agentPort = "";
		
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
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
				
					logger.info("About to wake up -> " + agentName + 
									   " on MAC -> " + agentMac + " with host of -> " +
									   agentHost + " and a port of -> " + agentPort);
					
					WakeOnLan wol = new WakeOnLan();
					wol.wakeUp(agentHost, agentMac);
					
					// Build AID name for the newly woken up name
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
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
					
					for (int i = 0; i < rsCount; ++i) {
						if (ignoreList.contains(result[i].getName())){
							logger.info("Agent found on ignore List ... skipping it ...");
							isMissing = false;
						} else {
							if (agentName.equalsIgnoreCase(result[i].getName().getLocalName())){
								jarvisAgents[i] = result[i].getName();
								isMissing = false;
							}
						}
					}
					if (isMissing){
						logger.info("About to wake up -> " + agentName + 
								   " on MAC -> " + agentMac + " with host of -> " +
								   agentHost + " and a port of -> " + agentPort);
						
						// call Wake on Lan class to wake up container
						WakeOnLan wol = new WakeOnLan();
						wol.wakeUp(agentHost, agentMac);
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
					agentMac = agentRS.getString("MAC");
					agentHost = agentRS.getString("host");
					agentPort = agentRS.getString("port");
					
					logger.info(getLocalName() + ": Agent Info" + 
									   "\n\tName:" + agentName + 
									   "\n\tMAC:" + agentMac +
									   "\n\tHost:" + agentHost + 
									   "\n\tPort:" + agentPort);
					
					for (int i = 0; i < result.length; ++i) {
						if (agentName.equalsIgnoreCase(result[i].getName().getLocalName())){
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
		if (command == SYSTEM_HALTING) {
			systemState = SYSTEM_HALTING;
			logger.info(getLocalName() + ": GUI sent Shutdown command.");
			alertGui(getSystemStateTxt());
			//doDelete();
			//System.exit(0);
		}
		else if (command == ADD_AGENT) {
			logger.fine(getLocalName() + ": GUI has requested adding a new agent.");
			// -------> Get parameters from event 
			
			// -------> Insert data into appropriate tables
			
			// -------> Launch Agent on remote platform
			
			// -------> Add agent communication behavior
		}
		else if (command == SECURITY_LEVEL_OFF){
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
		else if (command == SECURITY_LEVEL_NETWORK_AGENTS_ONLY){
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
		else if (command == SECURITY_LEVEL_ROBOT_AGENTS_ONLY){
			logger.fine(getLocalName() + ": GUI sent Security Robot Agents Only command.");
			
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
		else if (command == SECURITY_LEVEL_ALL_ON){
			logger.fine(getLocalName() + ": GUI sent Security On command.");
			
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
		else if (command == SYSTEM_RESET) {
			logger.info(getLocalName() + ": GUI sent System Reset command.");
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
}
