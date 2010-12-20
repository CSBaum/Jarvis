/**
 * 
 */
package net.stallbaum.jarvis;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class JarvisAndroidBehaviour extends TickerBehaviour implements
		SecurityVocabulary {

	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	private Jarvis jarvis;
	private int previousState;
	private int currentState;
	/**
	 * @param a
	 * @param period
	 */
	public JarvisAndroidBehaviour(Agent a, long period) {
		super(a, period);
		
		jarvis = (Jarvis)a;
		previousState = jarvis.getSystemState();
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		// Get the current state from Jarvis
		currentState = jarvis.getSystemState();
		if (checkDF()){
			// Check for state differences
			if (currentState != previousState){
				// Sen msg to ANdroid Agent to update status
				previousState = currentState;
			}
			
			// Check for alerts
		}
		else {
			if (currentState != previousState){
				previousState = currentState;
			}
		}

		
	}

	private boolean checkDF(){
		boolean isPresent = false;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jarvis-agent-android");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(jarvis, template);
			
			if (result.length > 0){
				logger.info("Located " + result.length + " Android agents");
				isPresent = true;
			}
		}catch (FIPAException fe) {
			logger.severe("FIPA Exception - " + fe.getLocalizedMessage());
		} 
		return isPresent;
	}
}
