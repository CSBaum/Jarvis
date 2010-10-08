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
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class AlerCommunicationBehvior extends TickerBehaviour implements
		SecurityVocabulary {
	
	String conversationId = "";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8937536645954360521L;
	CommunucationAgent cAgent = null;

	public AlerCommunicationBehvior(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		cAgent = (CommunucationAgent) a;
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
		
		if (cAgent.getState() != AGENT_INITIALIZING) {
			mt = MessageTemplate.and(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
													 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)),
								 MessageTemplate.and(MessageTemplate.MatchReceiver(values),
								                     MessageTemplate.MatchConversationId(cAgent.getConversationId())));
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
			if (cAgent.getSender() == null) { 
				cAgent.setSender(msg.getSender());
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
			
			// Do switch here
			switch(cAgent.getState()){
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
						//cAgent;
						System.out.println(cAgent.getLocalName() + ": Conversation ID is: " + conversationId);
					}
					break;
				}
			}
	}

}
