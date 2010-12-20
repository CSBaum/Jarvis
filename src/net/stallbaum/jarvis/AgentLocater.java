/**
 * 
 */
package net.stallbaum.jarvis;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class AgentLocater extends TickerBehaviour implements SecurityVocabulary {

	/**
	 * @param a
	 * @param period
	 */
	public AgentLocater(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub

	}

}
