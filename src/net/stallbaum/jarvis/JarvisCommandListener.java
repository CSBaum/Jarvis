/**
 * 
 */
package net.stallbaum.jarvis;

import java.util.StringTokenizer;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class JarvisCommandListener extends Behaviour implements
		SecurityVocabulary {

	private Jarvis jarvis = null;
	private boolean finished = false;

	public JarvisCommandListener() {
		super();
		jarvis = (Jarvis)myAgent;
		// TODO Auto-generated constructor stub
	}

	public JarvisCommandListener(Agent a) {
		super(a);
		jarvis = (Jarvis)a;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		/*
		 * This behaviour is looking for messages sent from outside the agent 
		 * pervue ... right now a manual message from the JADE gui is the 
		 * only source of messages here
		 */
		
		MessageTemplate mt = MessageTemplate.MatchConversationId("JADE-Control");
		ACLMessage msg = myAgent.receive(mt);

		if (msg != null) {
			ACLMessage reply = msg.createReply();
			String content = msg.getContent();
			if (content.equalsIgnoreCase(SYSTEM_HALT_MSG)){
				if (jarvis.lastSystemState != SYSTEM_HALTING){
					jarvis.lastSystemState = jarvis.systemState;
				}
				jarvis.systemState = SYSTEM_HALTING;
				reply.setPerformative(ACLMessage.CONFIRM);
				reply.setContent("Message received and processing.");
			}
			else if (content.contains(SYSTEM_SET_SECURITY_LEVEL_MSG)){
				// we need to parse the actual contents
				StringTokenizer st = new StringTokenizer(content, ":");
				String sysMsg = st.nextToken();
				int secLevel = Integer.getInteger(st.nextToken());
				
				switch (secLevel){
					case SECURITY_LEVEL_OFF:
						if (jarvis.lastSecurityLevel != secLevel) {
							// We need to set all agents to standy w/o sensors, etc.
						}
						else {
							// not sure is we really care that we received a dup msg
						}
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Message received and processing.");
						break;
					case SECURITY_LEVEL_ALL_ON:
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Message received and processing.");
						break;
					case SECURITY_LEVEL_NETWORK_AGENTS_ONLY:
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Message received and processing.");
						break;
					case SECURITY_LEVEL_ROBOT_AGENTS_ONLY:
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent("Message received and processing.");
						break;
					default:
						System.out.println(myAgent.getLocalName() + ": Invalid Securtiy Level");
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("Invalid Security Level");
						break;
				}
				//if (lastSystemState != SYSTEM_HALTING){
				//	lastSystemState = systemState;
				//}
				jarvis.systemState = SYSTEM_HALTING;
				reply.setPerformative(ACLMessage.CONFIRM);
				reply.setContent("Message received and processing.");
			}
			else {
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setContent("Invalid Message");
			}
			myAgent.send(reply);
		}
		block();
	}

	@Override
	public boolean done() {
		return finished;
	}

}
