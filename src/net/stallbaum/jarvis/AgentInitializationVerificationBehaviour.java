/**
 * 
 */
package net.stallbaum.jarvis;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class AgentInitializationVerificationBehaviour extends TickerBehaviour
		implements SecurityVocabulary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1581446583895119978L;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	Jarvis jarvis = null;
	int agentCount = 0;
	
	/**
	 * @param a
	 * @param period
	 */
	public AgentInitializationVerificationBehaviour(Agent a, long period) {
		super(a, period);
		jarvis = (Jarvis)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() { 
		// Dynamically update the count of valid agents we are listening to ...
		agentCount = jarvis.getInitializedAgentCount();
		
		if (agentCount > 0) {
			if (jarvis.systemState == SYSTEM_INITIALIZING){
				logger.info("Count = " + agentCount + " Vector Size = " + jarvis.agentListing.size());
				if (agentCount == jarvis.agentListing.size()){
					logger.info("Setting system state to Standby ....");
					jarvis.systemState = SYSTEM_STANDBY;
					jarvis.alertGui(jarvis.getSystemStateTxt());
				}
			}
		}
	}

}
