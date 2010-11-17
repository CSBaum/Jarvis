/**
 * 
 */
package net.stallbaum.jarvis;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class ShutdownAgent extends TickerBehaviour implements
		SecurityVocabulary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2337711331618263328L;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	Jarvis jarvis = null;
	int agentCount = 0;
	
	/**
	 * @param a
	 * @param period
	 */
	public ShutdownAgent(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		jarvis = (Jarvis) a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	public void onTick() {
		// Dynamically update the count of valid agents we are listening to ...
		agentCount = jarvis.agentListing.size();
		
		if (jarvis.systemState == SYSTEM_HALTING) {
			if (jarvis.agentListingSet.size() == jarvis.activeAgentsSet.size()){
				logger.warning(myAgent.getLocalName() + ": Shutting down");
				jarvis.takedown();
				myAgent.doDelete();
				System.exit(0);
			}
		}
	}

}
