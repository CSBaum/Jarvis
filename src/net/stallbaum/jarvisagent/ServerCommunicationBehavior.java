/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.IOException;
import java.util.ArrayList;

import jade.content.lang.xml.XMLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.AgentInitialization;
import net.stallbaum.jarvis.util.ontologies.MakeRobotOperation;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityLevel;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.SensorData;
import net.stallbaum.jarvis.util.ontologies.SystemMessage;

/**
 * @author sean
 *
 */
public class ServerCommunicationBehavior extends TickerBehaviour implements
		SecurityVocabulary {

	private static final long serialVersionUID = 5440333635234994943L;
	
	private JarvisAgent jAgent = null;
	private String conversationId; 
	private String alertId;
	
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	public ServerCommunicationBehavior(Agent a, long period){
		super(a, period);
		jAgent = (JarvisAgent)a;
	}
	
	public void onTick() {
		AID[] values = new AID[1];
		values[0] = myAgent.getAID();
		
		ACLMessage msg = null;
		ACLMessage reply = null;
		Object contentObj = null;
		int performative = 0;
		
		/*
		if ((jAgent.newSensordata != null)  && (jAgent.sensordata != null)) { 
			logger.info("jAgent know about " + jAgent.newSensordata.size() + " new sensor logs\n" + 
						"jAgent know about " + jAgent.sensordata.size() + " existing sensor logs");
		} */
		
		MessageTemplate mt = null;
		
		if (jAgent.agentState != AGENT_INITIALIZING) {
			mt = MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
													 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)),
								 MessageTemplate.and(MessageTemplate.MatchReceiver(values),
								                     MessageTemplate.MatchConversationId(conversationId)));
		}
		else {
			mt = MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
					 				 					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)),
				                     MessageTemplate.MatchReceiver(values));
		}
		
		msg = myAgent.receive(mt);

		if (msg != null) {	
			//-----> Do basic msg / reply setup
			reply = msg.createReply();
			if (jAgent.sender == null) { 
				jAgent.sender = msg.getSender();
			}
			performative = msg.getPerformative();
			System.out.println(myAgent.getLocalName() + ": Incoming Performative is: " + ACLMessage.getPerformative(performative));
			try {
				contentObj = msg.getContentObject();
				System.out.println(myAgent.getLocalName() + ": received the following message : ");
				System.out.println(msg.toString());
			} catch (UnreadableException ure) {
				System.out.println(myAgent.getLocalName() + ": Unable to process content.\n\t" + ure.getLocalizedMessage());
				Problem problem = new Problem(UNREADABLE_CONTENT, UNREADABLE_CONTENT_MSG);
				reply.setPerformative(ACLMessage.FAILURE);
				try {
					reply.setContentObject(problem);
				} catch (IOException e1) {
					System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
					System.out.println(e1.getLocalizedMessage());
					block();
				} catch (NullPointerException npe){
					System.out.println(myAgent.getLocalName() + ": Content was empty, going back to sleep.");
					System.out.println(npe.getLocalizedMessage());
					block();
				}
			}
			
			//-----> Process message based on agent state
			switch(jAgent.getState()){
				case AGENT_INITIALIZING:
					// Should we check to see if it is the correct Performative? (Inform)
					if (performative != ACLMessage.INFORM && performative != ACLMessage.REQUEST) {
						Problem problem = new Problem(INVALID_MSGTYPE, "Agent Initialization" + INVALID_MSGTYPE_MSG);
						reply.setPerformative(ACLMessage.FAILURE);
						try {
							reply.setContentObject(problem);
						} catch (IOException e1) {
							System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
							System.out.println(e1.getLocalizedMessage());
						}
					}
					else {
						// Get the conversation id so we can scope down the conversation :)
						conversationId = msg.getConversationId();
						jAgent.conversationId = conversationId;
						logger.fine(jAgent.getLocalName() + ": Conversation ID is: " + conversationId);
						
						// Check if INFORM type with robot object
						if (performative == ACLMessage.INFORM) {
							// Validate it's a robot obj to initialize from
							if (contentObj instanceof AgentInitialization) {
								AgentInitialization ai = (AgentInitialization)contentObj;
								jAgent.agentType = ai.getAgentType();
								
								// Initialize Player / Robot Agent based on info
								if (jAgent.agentType == ROBOT_AGENT){
									jAgent.agentRobot = ai.getRobot();
									System.out.println(myAgent.getLocalName() + ": Robot Information: " + jAgent.agentRobot.toString());
									
									// Launch PlayerAgent
									
									//--->Add PlayerBehavior (start up local instance of Player instance OR initialize a blank instance)
									//System.out.println("Adding HTTP client check");
									//jAgent.addBehaviour(new HttpCommunicationBehavior(jAgent));
								}
								else if (jAgent.agentType == NETWORK_AGENT){
									// TODO implement network agent here
								}
								else {
									// unimplemented remote agent initialization here ...
								}
								
								// Change Agent state to Standby
								jAgent.agentState = AGENT_STANDBY;
								System.out.println(myAgent.getLocalName() + ": CHANGING State to -- " + jAgent.getAgentStateTxt());
								
								// Reply to agent with success code (0)
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent("0");
							}
							else {
								System.out.println("Content is not an initialization object ...");
								Problem problem = new Problem();
								problem.setNum(10);
								problem.setMsg("Content type not understood");
								reply.setPerformative(ACLMessage.FAILURE);
								try {
									reply.setContentObject(problem);
								} catch (IOException e) {
									System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
									System.out.println(e.getLocalizedMessage());
									block();
								}
							}
						}
						else {
							if (contentObj instanceof SystemMessage){
								SystemMessage sysMsg = (SystemMessage)contentObj;
								System.out.println(myAgent.getLocalName() + ": Content is: " + sysMsg.toString());
								
								// Decide what we need to do 
								if (sysMsg.getMsgID() == SYSTEM_SET_SECURITY_LEVEL) {
									jAgent.checkSecurityLevel(sysMsg.getMsgSubId());
									System.out.println(myAgent.getLocalName() + ": Agent Security is now -- " + jAgent.getAgentStateTxt());
								}
								else if(sysMsg.getMsgID() == SYSTEM_HALT){
									System.out.println(myAgent.getLocalName() + ": Agent recieved shutdown message.");
									jAgent.agentState = AGENT_HALTING;
								}
								else {
									logger.warning("Unsupport System Message request: " + sysMsg.getMsgID());
								}
							}
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent("0");
						}
					}
					break;
				case AGENT_HALTING:
					// Reply that the agent is shutting down ...
					//    htis might not complete due to the fact that things are shutting down ..
					reply.setPerformative(ACLMessage.AGREE);
					reply.setContent("0: Agent already halting");
					break;
				case AGENT_STANDBY:
					if(msg.getPerformative() == ACLMessage.REQUEST){
						if (contentObj instanceof SystemMessage){
							SystemMessage sysMsg = (SystemMessage)contentObj;
							System.out.println(myAgent.getLocalName() + ": Content is: " + sysMsg.toString());
							
							// Decide what we need to do 
							if (sysMsg.getMsgID() == SYSTEM_SET_SECURITY_LEVEL) {
								jAgent.checkSecurityLevel(sysMsg.getMsgSubId());
								System.out.println(myAgent.getLocalName() + ": Agent Security Level is now -- " + jAgent.getAgentStateTxt());
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent("0");
							}
							else if (sysMsg.getMsgID() == SYSTEM_HALT){
								System.out.println(myAgent.getLocalName() + ": Agent recieved shutdown message.");
								jAgent.agentState = AGENT_HALTING;
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent("0");
							}
							else {
								System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + ":" + UNSUPPORTED_SYS_MSG_MSG);
								Problem problem = new Problem();
								problem.setNum(UNSUPPORTED_SYS_MSG);
								problem.setMsg(UNSUPPORTED_SYS_MSG_MSG);
								reply.setPerformative(ACLMessage.FAILURE);
								try {
									reply.setContentObject(problem);
								} catch (IOException e) {
									System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
									System.out.println(e.getLocalizedMessage());
									block();
								}
							}
						}
					}
					else if (msg.getPerformative() == ACLMessage.PROPOSE) {
						if (contentObj instanceof SystemMessage){
							SystemMessage sysMsg = (SystemMessage)contentObj;
							if (sysMsg.getMsgID() == SYSTEM_HALT){
								// Set agent state to halting
								System.out.println(myAgent.getLocalName() + ": Agent recieved shutdown message.");
								jAgent.agentState = AGENT_HALTING;
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent("0");
							}
							else {
								Problem problem = new Problem();
								problem.setNum(UNSUPPORTED_SYS_MSG);
								problem.setMsg(UNSUPPORTED_SYS_MSG_MSG);
								reply.setPerformative(ACLMessage.FAILURE);
								try {
									reply.setContentObject(problem);
								} catch (IOException e) {
									System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
									System.out.println(e.getLocalizedMessage());
									block();
								}
							}
						}
					}
					else
					{
						System.out.println(myAgent.getLocalName() + ": Agent Standby State");
						Problem problem = new Problem(INVALID_MSGTYPE, "Agent Standby - " + INVALID_MSGTYPE_MSG);
						reply.setPerformative(ACLMessage.FAILURE);
						try {
							reply.setContentObject(problem);
						} catch (IOException e1) {
							System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
							System.out.println(e1.getLocalizedMessage());
						}
					}
					break;
				case AGENT_ACTIVE:
					if (contentObj instanceof SystemMessage){
						SystemMessage sysMsg = (SystemMessage)contentObj;
						System.out.println(myAgent.getLocalName() + ": Content is: " + sysMsg.toString());
						
						// Decide what we need to do 
						if (sysMsg.getMsgID() == SYSTEM_SET_SECURITY_LEVEL) {
							jAgent.checkSecurityLevel(sysMsg.getMsgSubId());
							System.out.println(myAgent.getLocalName() + ": Agent Security is now -- " + jAgent.getAgentStateTxt());
						}
						else if (sysMsg.getMsgID() == SYSTEM_HALT){
							System.out.println(myAgent.getLocalName() + ": Agent recieved shutdown message.");
							jAgent.agentState = AGENT_HALTING;
						}
						
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("0");
					}
					break;
				default:
					//------> We shouldn't be here
					System.out.println(myAgent.getLocalName() + ": Invalid Agent State --- " + jAgent.agentState);
					Problem problem = new Problem(INVALID_AGENT_STATE,INVALID_AGENT_STATE_MSG);
					reply.setPerformative(ACLMessage.FAILURE);
					try {
						reply.setContentObject(problem);
					} catch (IOException e) {
						System.out.println(myAgent.getLocalName() + ": Unable to add problem object to reply.");
						System.out.println(e.getLocalizedMessage());
					}
					
			}
			//-----> Send reply to Jarvis
				if (reply.getContent() == null){
					logger.severe("We are trying to reply to Jarvis without any content.");
				}
				else {
					logger.info("Sending meesgae to Jarvis as default action: " + reply.getContent());
					myAgent.send(reply);
				}
		}
		else {
			block();
		}
		
		//-----> Check for alert status ...
		//TODO       THIS SHOULD GO AWAY ....
		/*
		if (jAgent.alertFound) {
			// we need to send out alert message
			ACLMessage alertMsg = new ACLMessage(ACLMessage.PROPAGATE);
			alertMsg.setSender(jAgent.getAID());
			alertMsg.setConversationId(jAgent.getAlertId());
			alertMsg.addReceiver(jAgent.getSender());
			//alertMsg.setContentObject(jAgent.al)
			
		}
		else if(jAgent.getAlertStatus()){
			// Pull the confirmation message from the agent and send a new message to Jarvis
			ACLMessage alertMsg = new ACLMessage(ACLMessage.CONFIRM);
			alertMsg.setSender(jAgent.getAID());
			alertMsg.addReceiver(jAgent.getSender());
			alertMsg.setConversationId(jAgent.getConversationId());
			try {
				alertMsg.setContentObject(jAgent.getAlertConfirmation());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			myAgent.send(alertMsg);
		}*/
		
		//------> Sensor data check and send if right agent state
		if ((jAgent.getState() == AGENT_ACTIVE) && 
				(jAgent.newSensordata.size() > 0)){		
			logger.info("Processing " + jAgent.newSensordata.size() + 
						" new data inputs");
			
			// Generate a new msg for each data obj and then remove 
			//       from the map and place on perm.
			Ontology ontology = SecurityOntology.getInstance();
			jAgent.getContentManager().registerOntology(ontology);
			jAgent.getContentManager().registerLanguage(new XMLCodec());
			
			//ACLMessage dataMsg = null;
			
			//for(SensorData data:jAgent.newSensordata){
			for(int jnx=0; jnx < jAgent.newSensordata.size(); jnx++) {
				SensorData data = jAgent.newSensordata.get(jnx);
				// Initialize Msg
				ACLMessage dataMsg = new ACLMessage(ACLMessage.INFORM);
				dataMsg.addReceiver(jAgent.getSender());
				dataMsg.setSender(jAgent.getAID());
				dataMsg.setLanguage(XMLCodec.NAME);
				dataMsg.setPerformative(ACLMessage.INFORM);
				dataMsg.setConversationId(jAgent.getConversationId());
		
				// Append data to msg
				try {
					dataMsg.setContentObject(data);
				} catch (IOException e) {
					logger.severe("Unable to attach sensor data to message:\n\tType: " + 
								   data.getType() + "\n\tTimeStamp:" );
				}
				
				// Sned msg
				logger.info("About to send message: " + dataMsg);
				myAgent.send(dataMsg);
				
				// Move data to perm list
				if (jAgent.sensordata == null){
					jAgent.sensordata = new ArrayList<SensorData>();
				}
				jAgent.sensordata.add(data);
				
				// Remove the item form teh new list
				jAgent.newSensordata.remove(jnx);
				jnx++;
			}
		}
		else {
			//logger.info("No new data or wrong state." + jAgent.getAgentState());
		}
	}
}
