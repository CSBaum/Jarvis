/**
 * 
 */
package net.stallbaum.jarvis;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Sean
 *
 */
public class MsgListening extends CyclicBehaviour {

	private JessBehavior jessBeh;
	
	MsgListening (Agent agent, JessBehavior _jessBeh) {
		super (agent);
		
		// save reference to the JessBehavior instance
		this.jessBeh = _jessBeh;
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// put into Jess Engine
			if (jessBeh.newMsg(msg)) {
				// do something
			}
			else {
				// do somethine else
			}
		}
		else
			block();
	}

}
