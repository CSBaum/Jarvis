/**
 * 
 */
package net.stallbaum.jarvis;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import jade.content.lang.xml.XMLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.AgentInitialization;
import net.stallbaum.jarvis.util.ontologies.Location;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.SensorData;
import net.stallbaum.jarvis.util.ontologies.SystemMessage;

/**
 * @author sean
 *
 */
public class JarvisAgentCommunication extends TickerBehaviour implements
		SecurityVocabulary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5601350228932185923L;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	private String convId = "";
	private AID targetAID;
	private int tickCount = 0;
	private boolean agentInitialized = false;
	private boolean agentNotified = false;
	private String alertId = "";
	
	private Robot robot;
	
	private Jarvis jarvis = null;
	
	private boolean finished = false;
	private boolean secChange = false;
	private boolean shutdownRequested = false;
	
	public JarvisAgentCommunication(Agent a, long period, AID _AID, Robot _robot, String _alert) {
		super(a, period);
		jarvis = (Jarvis)a;
		targetAID = _AID;
		convId = genCID();
		robot = _robot;
		alertId = _alert;
	}

	@Override
	protected void onTick() {
		// Check if there is any agent commands in the queue
		//     that need processing and continue, if not skip
		//     the cycle.
		logger.info("JarvisAgentCommunication with: " + targetAID.getLocalName());
		logger.fine("State: " + jarvis.getSystemStateTxt());
		
		//------> Handle System halt messages,etc
		switch(jarvis.systemState) {
			case SYSTEM_STANDBY:
				break;
			case SYSTEM_SECURITY_NETONLY:
				break;
			case SYSTEM_SECURITY_ROBOTONLY:
				break;
			case SYSTEM_SECURITY_ALL:
				break;
			case SYSTEM_HALTING:
				if (!agentNotified) {
					logger.warning(getBehaviourName() + " - System halting ... ");
					
					Ontology ontology = SecurityOntology.getInstance();
					myAgent.getContentManager().registerOntology(ontology);
					myAgent.getContentManager().registerLanguage(new XMLCodec());
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setSender(myAgent.getAID());
					msg.addReceiver(targetAID);
					msg.setLanguage(XMLCodec.NAME);
					msg.setConversationId(convId);
		
					SystemMessage sysMsg = new SystemMessage();
					sysMsg.setMsgID(SYSTEM_HALT);
					try {
						msg.setContentObject(sysMsg);
					} catch (IOException e) {
						logger.severe("Unable to add System Message object to reply to " + targetAID.getLocalName() + ".");
						logger.severe(e.getLocalizedMessage());
						block();
					}
		
					myAgent.send(msg);
					msg = null;
					logger.info(myAgent.getLocalName() +": Sent system halt message to " + targetAID.getLocalName());
					agentNotified = true;
					shutdownRequested = true;
				}
				else
				{
					logger.info(myAgent.getLocalName() + ": Waiting to shutdown...");
				}
				break;
			default:
		}
		
		if (jarvis.securityLevel != jarvis.lastSecurityLevel){
			logger.config(getBehaviourName() + ": Detected security level difference. ");
			
			// -----> Send security level change message
			Ontology ontology = SecurityOntology.getInstance();
			jarvis.getContentManager().registerOntology(ontology);
			jarvis.getContentManager().registerLanguage(new XMLCodec());
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setSender(jarvis.getAID());
			msg.addReceiver(targetAID);
			msg.setLanguage(XMLCodec.NAME);
			msg.setConversationId(convId);
		
			SystemMessage sysMsg = new SystemMessage();
			sysMsg.setMsgID(SYSTEM_SET_SECURITY_LEVEL);
			sysMsg.setMsgSubId(jarvis.securityLevel);
			
			try {
				msg.setContentObject(sysMsg);
			} catch (IOException e) {
				logger.severe("Unable to attach system message for " + targetAID.getLocalName() +  " - "  + e.getLocalizedMessage());
			}
			
			myAgent.send(msg);
			msg = null;
			logger.config("Sent Security Level change to " + targetAID.getLocalName());
			secChange = true;
		}
		
		//-----> Test & security level cleanup
		if (tickCount % 10 == 0){
			if (!agentInitialized){
				Ontology ontology = SecurityOntology.getInstance();
				jarvis.getContentManager().registerOntology(ontology);
				jarvis.getContentManager().registerLanguage(new XMLCodec());
				
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(jarvis.getAID());
				msg.addReceiver(targetAID);
				msg.setLanguage(XMLCodec.NAME);
				msg.setConversationId(convId);

				Location loc = new Location();
				
				AgentInitialization ai = new AgentInitialization();
				ai.setAgentType(ROBOT_AGENT);
				ai.setRobot(robot);
				ai.setLoc(loc);
				ai.setAlertId(alertId);
	
				try {
					msg.setContentObject(ai);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				myAgent.send(msg);
				msg = null;
				logger.info("Send Robot Object to " + targetAID.getLocalName());
			}
		}

		tickCount++;
		
		//------> Check for msgs
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(targetAID),
												 MessageTemplate.MatchConversationId(convId));
		ACLMessage msg = myAgent.receive(mt);
		
		if (msg != null) {
			int performative = msg.getPerformative();

			if (performative == ACLMessage.FAILURE){
				try {
					Problem problem = (Problem)msg.getContentObject();
					problem.setAgentName(targetAID.getLocalName());
					logger.warning(getBehaviourName() + ": An error occured with Agent, " + msg.getSender().getName()
									   + "\n\t: Agent: " + problem.getAgentName()
									   + "\n\t: Error Code: " + problem.getNum()
									   + "\n\t: Error Message: " + problem.getMsg());
					jarvis.alertGui(problem);
				} catch (UnreadableException ure) {
					logger.warning("Unable to process content.\n\t" + ure.getLocalizedMessage());
					//System.out.println(myAgent.getLocalName() + ": Unable to process content.\n\t" + ure.getLocalizedMessage());
					ure.printStackTrace();
				}
			}
			else if (performative == ACLMessage.CONFIRM){
				SystemMessage sysMsg = null;
				try {
					sysMsg = (SystemMessage)msg.getContentObject();
				} catch (UnreadableException e) {
					logger.severe("Unable to retrieve System Message from Agent: " + e.getLocalizedMessage());
				}
				
				if (secChange){
					switch(jarvis.securityLevel){
						case SECURITY_LEVEL_OFF: logger.info("Security turned off ...");
												 if (sysMsg.getMsgSubId() != AGENT_STANDBY){
													 logger.warning("The agent is not reporting the correct status:" + sysMsg.getMsgSubId());
												 }
												 break;
						case SECURITY_LEVEL_ALL_ON:	if (sysMsg.getMsgSubId() != AGENT_ACTIVE){
														logger.warning("The agent is not reporting the correct status:" + sysMsg.getMsgSubId());
													}
													break;
						default: logger.warning("We should never be haere ...");
								 break;
					}
				}
				else if (sysMsg.getMsgSubId() == AGENT_INITIALIZED){
					agentInitialized = true;
					jarvis.addActiveAgent(myAgent.getAID());
					jarvis.addInitializedAgent(myAgent.getAID());
					logger.config(myAgent.getLocalName() + ": Adding new agent to Active Agent Set ...");
				}
			}
			else if(performative == ACLMessage.AGREE) {
				SystemMessage sysMsg = null;
				try {
					sysMsg = (SystemMessage)msg.getContentObject();
				} catch (UnreadableException e) {
					logger.severe("Unable to retrieve System Message from Agent: " + e.getLocalizedMessage());
				}
				if (sysMsg.getMsgID() == AGENT_ACK){
					if (sysMsg.getMsgSubId() == AGENT_HALTING){
						logger.fine(myAgent.getLocalName() + ": Agent " + msg.getSender().getLocalName() + "has agreed to shutdown.");
						jarvis.agentListingSet.add(msg.getSender());
						logger.info(myAgent.getLocalName() + ":" + getBehaviourName() + " - New count of responding agents - " + jarvis.agentListingSet.size());
						agentNotified = true;
					}
					else {
						logger.warning("Invalid System Message SubID: " + sysMsg.getMsgSubId());
					}
				}
				else
					logger.warning("Invalid AGREE message from " + msg.getSender().getLocalName());
			}
			else if(performative == ACLMessage.INFORM){
				// ------> Agent is sending data back
				logger.info("Agent has recieved an INFORM msg");
				try {
					if (msg.getContentObject() instanceof SensorData){
						SensorData data = (SensorData)msg.getContentObject();
						logger.fine("Recieved the following sensor data: " + data);
						
						//----> Send data to Jess Agent
						
						//----> Check if archived fleg is set
						if(data.getIsArchived()){
							logger.info("Archiving data ...");
							jarvis.archive.archiveData(data);
							/*
							logger.info("Attempting to retrieve data ....");
							Date startDate = new GregorianCalendar(1994, 02, 14, 14, 00).getTime();
							Date endDate = Calendar.getInstance().getTime();
							logger.info("StartDate: " + startDate + " EndDate: " + endDate);
							Vector<SensorData> requestedData = jarvis.archive.retrieveData(targetAID, 1, 1, startDate, endDate);
							logger.info("We found " + requestedData.size() + " matching items.");*/
						}
					}
					else
					{
						if (msg.getContentObject() != null)
							logger.warning("Received the object type: " + msg.getContentObject().getClass().getName());
					}
				} catch (UnreadableException e) {
					logger.warning("Unable to read message content" + e.getLocalizedMessage());
				}
			}
			else {
				logger.warning("Received unhandled msg of type: " + ACLMessage.getPerformative(performative) + "\n" +
							   "\tFrom: " + msg.getSender().getLocalName() + "\n" + 
							   "\tContaining: " + msg.getContent());
			}
		}
		else {
			block();
		}
	}
			
//  --- generating Conversation IDs -------------------

	protected int cidCnt = 0;
	String cidBase ;

	private String genCID() 
	{ 
		if (cidBase==null) {
			cidBase = myAgent.getLocalName() + hashCode() +
			System.currentTimeMillis()%10000 + "_";
		}
		return  cidBase + (cidCnt++); 
	}

	//  --- generating distinct Random generator -------------------

	private Random newRandom() 
	{	return  new Random( hashCode() + System.currentTimeMillis()); }
}
