/**
 * 
 */
package net.stallbaum.jarvis;

import java.io.IOException;
import java.util.Random;

import jade.content.abs.AbsObject;
import jade.content.lang.xml.XMLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import net.stallbaum.jarvis.util.ontologies.AgentInitialization;
import net.stallbaum.jarvis.util.ontologies.Location;
import net.stallbaum.jarvis.util.ontologies.MakeRobotOperation;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityLevel;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.SystemMessage;

/**
 * @author sean
 *
 */
public class JarvisAgentCommunication extends TickerBehaviour implements
		SecurityVocabulary {

	private String convId = "";
	private AID targetAID;
	private int tickCount = 0;
	private boolean agentInitialized = false;
	private boolean agentNotified = false;
	
	private Jarvis jarvis = null;
	
	private boolean finished = false;
	
	public JarvisAgentCommunication(Agent a, long period, AID _AID) {
		super(a, period);
		jarvis = (Jarvis)a;
		targetAID = _AID;
		convId = genCID();
	}

	@Override
	protected void onTick() {
		// Check if there is any agent commands in the queue
		//     that need processing and continue, if not skip
		//     the cycle.
		System.out.println("JarvisAgentCommunication with: " + targetAID.getLocalName());
		System.out.println(myAgent.getLocalName() + ": state: " + jarvis.getSystemStateTxt());
		
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
					System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - System halting ... ");
		
					XMLCodec codex = new XMLCodec();
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
						System.out.println(myAgent.getLocalName() + ": Unable to add System Message object to reply.");
						System.out.println(e.getLocalizedMessage());
						block();
					}
		
					myAgent.send(msg);
					System.out.println(myAgent.getLocalName() +": Sent system halt message to " + targetAID.getLocalName());
					agentNotified = true;
					//finished = true;
					//stop();
				}
				else
				{
					System.out.println(myAgent.getLocalName() + ": Waiting to shutdown...");
				}
				break;
			default:
		}
		
		if (jarvis.securityLevel != jarvis.lastSecurityLevel){
			System.out.println(getBehaviourName() + ": Detected security level difference. ");
			
			// -------> Send security level change message
			XMLCodec codex = new XMLCodec();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			myAgent.send(msg);
			System.out.println(myAgent.getLocalName() +": Send Security Level change to " + targetAID.getLocalName());
		}
		
		/*if (jarvis.systemState == SYSTEM_HALTING){
			if (!agentNotified) {
				// -------> Send security level change message
				XMLCodec codex = new XMLCodec();
				Ontology ontology = SecurityOntology.getInstance();
				jarvis.getContentManager().registerOntology(ontology);
				jarvis.getContentManager().registerLanguage(new XMLCodec());
				
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.setSender(jarvis.getAID());
				msg.addReceiver(targetAID);
				msg.setLanguage(XMLCodec.NAME);
				msg.setConversationId(convId);
			
				SystemMessage sysMsg = new SystemMessage();
				sysMsg.setMsgID(SYSTEM_HALT);
				
				try {
					msg.setContentObject(sysMsg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				myAgent.send(msg);
				System.out.println(myAgent.getLocalName() +": Sent System Halt message to agent " + targetAID.getLocalName());
			}
		}*/
		
		// ---------------- Test & security level cleanup
		if (tickCount % 10 == 0){
			if (!agentInitialized){
				XMLCodec codex = new XMLCodec();
				Ontology ontology = SecurityOntology.getInstance();
				jarvis.getContentManager().registerOntology(ontology);
				jarvis.getContentManager().registerLanguage(new XMLCodec());
				
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(jarvis.getAID());
				msg.addReceiver(targetAID);
				msg.setLanguage(XMLCodec.NAME);
				msg.setConversationId(convId);
	
				Robot r1 = new Robot();
				r1.setName("Roomba1");
				r1.setHasAudio(false);
				r1.setHasSonar(false);
				
				Location loc = new Location();
				
				AgentInitialization ai = new AgentInitialization();
				ai.setAgentType(ROBOT_AGENT);
				ai.setRobot(r1);
				ai.setLoc(loc);
	
				try {
					msg.setContentObject(ai);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				myAgent.send(msg);
				System.out.println(myAgent.getLocalName() +": Send Robot Object to " + targetAID.getLocalName());
				//System.out.println(myAgent.getLocalName() +": Message was: " + msg);
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
					System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + ": An error occured with Agent, " + msg.getSender().getName()
									   + "\n\t: Error Code: " + problem.getNum()
									   + "\n\t: Error Message: " + problem.getMsg());
				} catch (UnreadableException ure) {
					System.out.println(myAgent.getLocalName() + ": Unable to process content.\n\t" + ure.getLocalizedMessage());
					ure.printStackTrace();
				}
			}
			else if (performative == ACLMessage.CONFIRM){
				System.out.println(myAgent.getLocalName() + ": Agent " + msg.getSender().getLocalName() + " has confirmed last message success.");
				agentInitialized = true;
				jarvis.addActiveAgent(myAgent.getAID());
				System.out.println(myAgent.getLocalName() + ": Adding new agent to Active Agent Set ...");
				// Based on system state & content, do something :)
			}
			else if(performative == ACLMessage.AGREE) {
				System.out.println(myAgent.getLocalName() + ": Agent " + msg.getSender().getLocalName() + "has agreed to shutdown.");
				jarvis.agentListingSet.add(msg.getSender());
				System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - New count of responding agents - " + jarvis.agentListingSet.size());
				agentNotified = true;
			}
			else if(performative == ACLMessage.INFORM){
				// ------> Agent is sending data back
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
