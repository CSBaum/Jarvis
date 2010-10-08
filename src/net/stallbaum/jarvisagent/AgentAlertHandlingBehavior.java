/**
 * 
 */
package net.stallbaum.jarvisagent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class AgentAlertHandlingBehavior extends TickerBehaviour implements
		SecurityVocabulary {

	AbsJAgent agent;
	
	/**
	 * @param a
	 * @param period
	 */
	public AgentAlertHandlingBehavior(Agent a, long period) {
		super(a, period);
		
		agent = (AbsJAgent)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		
		//----> Check for messages that match the alert id
		
		//----> Change status / heuristics / etc to reflect that an agent has alerted
		
		//----> Set flag so that Server Comm agent sends confirmation msg
		//			NOTE: if status cheanges fail, a status flag should reflect that in
		//				  the AlertConfirmation Object
		//agent.setAlertStataus(true);

	}

}
