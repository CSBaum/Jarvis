/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class AgentPlayerCommBehavior extends TickerBehaviour implements
		SecurityVocabulary {

	//------> Variables
	String conversationId;
	
	PlayerAgent pAgent = null;
	/**
	 * @param a
	 * @param period
	 */
	public AgentPlayerCommBehavior(Agent a, long period) {
		super(a, period);
		pAgent = (PlayerAgent)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		AID[] values = new AID[1];
		values[0] = myAgent.getAID();
		
		ACLMessage msg = null;
		ACLMessage reply = null;
		Object contentObj = null;
		int performative = 0;

		MessageTemplate mt = null;
		
		System.out.println(myAgent.getLocalName() + ": Agent State: " + pAgent.agentState);
		
		if (pAgent.agentState != AGENT_INITIALIZING) {
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
			
			//-----> Debug
			//System.out.println(myAgent.getLocalName() + ": AgentState = " + jAgent.agentState);
			
			//-----> Do basic msg / reply setup
			reply = msg.createReply();
			performative = msg.getPerformative();
			System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Incoming Performative is: " + ACLMessage.getPerformative(performative));
			
			try {
				contentObj = msg.getContentObject();
				System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - received the following message : ");
				System.out.println(msg.toString());
			} catch (UnreadableException ure) {
				System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Unable to process content.\n\t" + ure.getLocalizedMessage());
				Problem problem = new Problem(UNREADABLE_CONTENT, UNREADABLE_CONTENT_MSG);
				reply.setPerformative(ACLMessage.FAILURE);
				try {
					reply.setContentObject(problem);
				} catch (IOException e1) {
					System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Unable to add problem object to reply.");
					System.out.println(e1.getLocalizedMessage());
					block();
				} catch (NullPointerException npe){
					System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Content was empty, going back to sleep.");
					System.out.println(npe.getLocalizedMessage());
					block();
				}
			}
			
			// Now we need ot sort through the various agent states :)
			switch (pAgent.agentState) {
				case AGENT_INITIALIZING:
					if (contentObj instanceof Robot){
						
					}
					else {
						
					}
					pAgent.isInitialized = true;
					break;
				case AGENT_HALTING:
					// Reply that the agent is shutting down ...
					//    htis might not complete due to the fact that things are shutting down ..
					reply.setPerformative(ACLMessage.AGREE);
					reply.setContent("0: Agent already halting");
					break;
				case AGENT_STANDBY:
					break;
				default:
			}
		}
		else {
			block();
		}

	}

}
